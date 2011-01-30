from django.db import models
from django.contrib.auth.models import User
from pushprototypedjango import settings
import os

# An android device which has registered with our server and can receive push notifications
class Device(models.Model):
    identifier = models.CharField(max_length=200) # uid of phone/device
    registrationid = models.CharField(max_length=200) # c2dm registration id forwarded by phone
    user = models.ForeignKey(User, null=True, blank=True) # would be nice to tie to user, but this is a prototype after all

# A message which has been sent (or is too be sent) to a device
class Message(models.Model):
    content = models.CharField(max_length=2400) # message contents
    status = models.IntegerField(null=True, blank=True) # status of message (0, not pushed; 1, pushed; 2, retrieved or "sent")
    device = models.ForeignKey(Device)
    
    def filepath(self):
        return os.path.join(settings.MEDIA_ROOT, str(self.pk)+".mp4")
    
    def isaudio(self):
        return os.path.exists(Message.filepath(self))
