from django.db import models
from django.contrib.auth.models import User

class Device(models.Model):
    identifier = models.CharField(max_length=200)
    registrationid = models.CharField(max_length=200)
    currenttoken = models.CharField(max_length=200, null=True, blank=True)
    user = models.ForeignKey(User, null=True, blank=True)
 
class Message(models.Model):
    content = models.CharField(max_length=2400)
    status = models.IntegerField(null=True, blank=True)
    device = models.ForeignKey(Device)