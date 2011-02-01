from django.http import HttpResponse, HttpResponseRedirect
from django.http import HttpResponseRedirect
from models import Device, Msg
from django.contrib.auth.decorators import login_required
from django.contrib.auth import authenticate, login, logout

from django.core.files.base import ContentFile

from django.shortcuts import get_object_or_404, render_to_response
from django.views.decorators.csrf import csrf_exempt
try:
    from pushprototypedjango.push.authentication import GOOGLE_TOKEN
except:
    # this is a hack for the continuous integration server -adamg 1/31
    GOOGLE_TOKEN = 'bogus1234'
from django import forms
from django.template.context import RequestContext
from django.views.generic.simple import redirect_to
import httplib
import urllib
import os.path

def logout_view(request):
    logout(request)
    return HttpResponseRedirect('/')

@csrf_exempt # this is needed to get around use of csrf (not good for live app)
def login_view(request):
    if request.method == 'POST':
        username = request.POST['username']
        password = request.POST['password']
        user = authenticate(username=username, password=password)
        if user is not None:
            if user.is_active:
                login(request, user)
                # Redirect to a success page
                return HttpResponseRedirect('/')
            else:
                # Return a 'disabled account' error message
                pass
        else:
            # Return an 'invalid login' error message.
            pass
    else:
        return render_to_response('login.html',
                                {'form':LoginForm()},
                                context_instance=RequestContext(request))

# default view, lists devices with number of messages sent
@login_required
def list(request):
    devices = Device.objects.all()
    return render_to_response('list.html', {'devices': devices, 'user':request.user},
                            context_instance=RequestContext(request))

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
        m = Msg.objects.create(device=device, content=request.POST["message"])
        return message_send(request, m.pk)

    # not a post, let's pass a MessageForm object to create the form
    options = {'device': device, 'form':MessageForm()}
    return render_to_response('message_form.html', options,context_instance=RequestContext(request))

def message_send(request, messageid):
    message = get_object_or_404(Msg, pk=messageid)

    # NOW SEND THE REQUEST TO GOOGLE SERVERS
    # first we need an https connection that ignores the certificate (for now)
    httpsconnection = httplib.HTTPSConnection("android.apis.google.com", 443)

    # we need the following params set per http://code.google.com/android/c2dm/index.html#push
    params = urllib.urlencode({
             'registration_id': message.device.registrationid,
             'collapse_key': "message"+str(message.id),
             'data.message': str(message.id),
             'delay_when_idle':'TRUE',
             })
    # need the following headers set per http://code.google.com/android/c2dm/index.html#push
    headers = { "Content-Type":"application/x-www-form-urlencoded",
                "Content-Length":len(params),
                "Authorization":"GoogleLogin auth=" + GOOGLE_TOKEN # TOKEN set manually in authentication.py
                }

    httpsconnection.request("POST", "/c2dm/send", params, headers)

    # assuming success, let's return the user to the device list for now
    return redirect_to(request, "/", False)


@csrf_exempt
def message_recieve(request):
    print "GOT IT!"
    if "audio" in request.FILES and "phoneid" in request.POST:
        print "Getting device..."
        device = get_object_or_404(Device, identifier=request.POST["phoneid"])
        m = Msg.objects.create(device=device, content='AUDIO')
        file_content = ContentFile(request.FILES['audio'].read())
        file_format = os.path.splitext( request.FILES['audio'].name )[-1]
        try:
            reciever = m.recipient.username
        except:
            reciever = 'unknown'
        try:
            sender = m.sender.username
        except:
            sender = 'unknown'
        filename = "%s2%s@%s%s" % (sender, 
                                    reciever, 
                                    m.created.strftime("%H%M%S"),
                                    file_format)
        m.audio.save(filename, file_content)
        m.save()
        return HttpResponse("POISTED!", None, 200)

    # not a post, let's pass a MessageForm object to create the form
    #options = {'device': device, 'form':MessageForm()}
    #return render_to_response('message_form.html', options,context_instance=RequestContext(request))
    return HttpResponse("DAMMIT", None, 403)

@csrf_exempt
def message_deliver(request, message_id=0):
    if "messageid" in request.POST:
        message_id = int(request.POST["messageid"])
    
    print "Getting message..."
    m = get_object_or_404(Msg, pk=message_id)
    #TODO: need to put some checking for correct device here eventually

    if m.isaudio():
        print "audio message..."
        m.audio.open(mode='rb')
        contents = m.audio.file
        h = HttpResponse(contents.read(),"audio/mp4",200,"audio/mp4")
        h["Content-Disposition"]= "attachment; filename=%s" % m.audio.name
    else:
        h = HttpResponse(m.content)
    return h

class MessageForm(forms.Form):
    message = forms.CharField(max_length=1024)

class LoginForm(forms.Form):
    username = forms.CharField(label=(u'Username'))
    password = forms.CharField(label=(u'Password'),widget=forms.PasswordInput(render_value=False))