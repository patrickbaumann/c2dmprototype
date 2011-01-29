from django.http import HttpResponse
from models import Device 
from django.shortcuts import get_object_or_404, render_to_response
from django.views.decorators.csrf import csrf_exempt
from pushprototypedjango.push.authentication import GOOGLE_TOKEN
from django import forms
from django.template.context import RequestContext
from django.views.generic.simple import redirect_to
import httplib
import urllib

# default view, lists devices with number of messages sent
def list(request):
    devices = Device.objects.all()
    return render_to_response('list.html', {'devices': devices})

# shows more info about a device
def show(request, identifierid):
    device = get_object_or_404(Device, identifier=identifierid)
    return render_to_response('show.html', {'device': device})

# register a device with this server (post)
@csrf_exempt # this is needed to get around use of csrf (not good for live app)
def register(request):
    try: #to access the POST object via a potentially nonexistant key
        regid = request.POST["registrationid"]
        print regid
        phoneid = request.POST["phoneid"]
    except KeyError:
        return HttpResponse("Bad Request", None, 400)
    
    try:
        device = Device.objects.get(identifier=phoneid)
    except Device.DoesNotExist:
        device = Device.objects.create(identifier=phoneid)
    
    device.registrationid = regid
    device.save() # we just want to save and move on.
    return HttpResponse("Success!")

# create a message object and and send to the c2dm server (post)
# provide the user with a form to post (get)
def create_message(request, identifierid):
    device = get_object_or_404(Device, pk=identifierid)
    
    if "message" in request.POST:
        message = device.message_set.create(content=request.POST["message"])
        
        # NOW SEND THE REQUEST TO GOOGLE SERVERS
        # first we need an https connection that ignores the certificate (for now)
        httpsconnection = httplib.HTTPSConnection("android.apis.google.com", 443)
        
        # we need the following params set per http://code.google.com/android/c2dm/index.html#push
        params = urllib.urlencode({
                 'registration_id': device.registrationid,
                 'collapse_key': "message"+str(message.id),
                 'data.message': str(message.content)
                 })
        # need the following headers set per http://code.google.com/android/c2dm/index.html#push
        headers = { "Content-Type":"application/x-www-form-urlencoded",
                    "Content-Length":len(params),
                    "Authorization":"GoogleLogin auth=" + GOOGLE_TOKEN # TOKEN set manually in authentication.py
                    }
        
        httpsconnection.request("POST", "/c2dm/send", params, headers)
        
        # assuming success, let's return the user to the device list for now
        return redirect_to(request, "/", False)
        
    # not a post, let's pass a MessageForm object to create the form
    options = {'device': device, 'form':MessageForm()}
    return render_to_response('message_form.html', options,context_instance=RequestContext(request))
    
@csrf_exempt
def message_recieve(request):
    print "GOT IT"
    print request.POST
    if "audio" in request.POST and "phoneid" in request.POST:
        device = get_object_or_404(Device, identifier=request.POST["phoneid"])
        message = device.message_set.create(content=request.POST["audio"])
        return HttpResponse("POISTED!", None, 200)
        
    # not a post, let's pass a MessageForm object to create the form
    #options = {'device': device, 'form':MessageForm()}
    #return render_to_response('message_form.html', options,context_instance=RequestContext(request))
    return HttpResponse("DAMMIT", None, 403)
        
class MessageForm(forms.Form):
    message = forms.CharField(max_length=1024)
    
