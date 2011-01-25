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

def list(request):
    devices = Device.objects.all()
    return render_to_response('list.html', {'devices': devices})

def show(request, identifierid):
    device = get_object_or_404(Device, identifier=identifierid)
    return render_to_response('show.html', {'device': device})

@csrf_exempt # this is needed to get around use of csrf (not good for live app
def register(request):
    try:
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
    device.save()
    return HttpResponse("Success!")

def create_message(request, identifierid):
    device = get_object_or_404(Device, pk=identifierid)
    if "message" in request.POST:
        message = device.message_set.create(content=request.POST["message"])
        # NOW SEND THE REQUEST TO GOOGLE SERVERS
        
        httpsconnection = httplib.HTTPSConnection("android.apis.google.com", 443)
        params = urllib.urlencode({
                 'registration_id': device.registrationid,
                 'collapse_key': "message"+str(message.id),
                 'data.message': str(message.content)
                 })
        headers = { "Content-Type":"application/x-www-form-urlencoded",
                    "Content-Length":len(params),
                    "Authorization":"GoogleLogin auth=" + GOOGLE_TOKEN
                    }
        print params, GOOGLE_TOKEN
        
        httpsconnection.request("POST", "/c2dm/send", params, headers)
        #response = httpsconnection.getresponse()
        
        return redirect_to(request, "/", False)
        
    options = {'device': device, 'form':MessageForm()}
    return render_to_response('message_form.html', options,context_instance=RequestContext(request))
    

class MessageForm(forms.Form):
    message = forms.CharField(max_length=1024)