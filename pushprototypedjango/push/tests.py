"""
This file demonstrates two different styles of tests (one doctest and one
unittest). These will both pass when you run "manage.py test".

Replace these with more appropriate tests for your application.
"""

from django.test import TestCase
from django.contrib.auth.models import User
from models import Msg, Device
import random
import array
import os

class TestMyModels(TestCase):
    fixtures = ['User.json', 'push.json']
    def setUp(self):
        pass

    def tearDown(self):
        pass
        
    def testDeviceValidation(self):
        """ Test Device Validations """
        
        numDevices = len(Device.objects.all())
        # device = random.choice(Device.objects.all())
        device = Device(registrationid=1234)
        device.identifier = '0'
        try:
            device.save()
        except:
            pass
        newNumDevices = len(Device.objects.all())
        self.assertEqual(numDevices + 1, newNumDevices, "Device Model Validation is not working.")
        
    def testDeleteMsg(self):
        """ Delete Messages from Device """
        
        #msg = random.choice(Msg.objects.all())
        nummsg = len(Msg.objects.all())
        msg = Msg.objects.all()[1]
        device = msg.device
        pk = msg.pk
        msg.delete()
        newnummsg = len(Msg.objects.all())
        #self.assertRaises(Msg.DoesNotExist, device.msg_set.get(id=pk))
        self.assertEqual(newnummsg + 1, nummsg, "Could not delete a message")
        
    def testNewMsgForDevice(self):
        """ Add a new msg to a device """
        
        #device = random.choice(Device.objects.all())
        device = Device.objects.all()[1]
        msgCnt = len(device.msg_set.all())
        newMsg = Msg.objects.create(content='This is a message for a random Device',
                                    status=2, 
                                    device=device,
                                    recipient=User.objects.all()[0],
                                    sender=User.objects.all()[1])
        newMsgCnt = len(device.msg_set.all())
        self.assertEqual(msgCnt+1, newMsgCnt, "Problem adding new msg to device.")

# Django.test.TestCase Assertions:
# TestCase.assertContains(response, text, count=None, status_code=200, msg_prefix='')
# TestCase.assertNotContains(response, text, status_code=200, msg_prefix='')
# TestCase.assertFormError(response, form, field, errors, msg_prefix='')
# TestCase.assertTemplateUsed(response, template_name, msg_prefix='')
# TestCase.assertTemplateNotUsed(response, template_name, msg_prefix='')
# TestCase.assertRedirects(response, expected_url, status_code=302, target_status_code=200, msg_prefix='')
# TestCase.assertRedirects(response, expected_url, status_co

class TestLoggedInViews(TestCase):
    fixtures = ['User.json', 'push.json']
    def setUp(self):
        pass

    def tearDown(self):
        pass
        
    def test_index(self):
        """ Test that we are forced to login to view webroot """
        
        response = self.client.get('/')
        # expect redirect to the login page:
        self.assertEqual(response.status_code, 302, "We didnt have to login to see the index page")
        geocamUser = User.objects.get(username='geocam')
        self.assertTrue(self.client.login(username='geocam',
                                        password='geocam'))
        response = self.client.get('/')
        # expect success because we are logged in:
        self.assertEqual(response.status_code, 200, "Loged in user cant see index page")                                

    def test_login(self):
        """ Make sure all users can login """
        
        for u in User.objects.all():
            self.assertTrue(self.client.login(username=u.username, password='geocam'))
                
    def testAudioMsgCreate(self):
        """ Add a Msg by http post"""
        CMUSV_LAT = 37.41029
        CMUSV_LON = -122.05944
        #device = random.choice(Device.objects.all())
        device = Device.objects.all()[0]
        phoneid = device.identifier
        audioFile = 'test.mp4'
        self._createFile(filename=audioFile, filesize=100*1024)
        f = open(audioFile, "rb")
        response = self.client.post("/push/message/",
                                    data={'phoneid':phoneid, 'audio':f, 'lat':CMUSV_LAT, 'lon':CMUSV_LON})
        f.close() 
        self.assertEqual(response.status_code, 200, "Failed to move message from phone to web app")

    def testTextMsgCreate(self):
        """ Add a Msg by http post"""
        pass

    def testMsgShow(self):
        pass
        
    def testMsgList(self):
        pass

    def testMsgDeliver(self):
        pass

    def testMsgSend(self):
        pass

    def testRegister(self):
        # we shouldn't ping google on this one
        pass

    def testAudioRecieve(self):
        pass
        
    def testAudioSend(self):
        pass

    def _createFile(self, filename, filesize=5*1024*1024):
        """Create and fill a file with random data"""
        blocksize = 4096 # 4k
        datablock = array.array('I')
        written = 0
        # Create a datablock:
        while written < blocksize:
            datablock.append(random.getrandbits(32))
            written = written + 4
            
        with open(filename, 'w') as f:
            written = 0
            while written < filesize:
                datablock.tofile(f)
                written += blocksize
            f.flush()
            os.fsync(f.fileno())

# unittest.TestCase Assertions:
# assertTrue(expr[, msg])
# assert_(expr[, msg])
# failUnless(expr[, msg])
# assertEqual(first, second[, msg])
# failUnlessEqual(first, second[, msg])
# assertNotEqual(first, second[, msg])
# failIfEqual(first, second[, msg])
# assertAlmostEqual(first, second[, places[, msg[, delta]]])
# failUnlessAlmostEqual(first, second[, places[, msg[, delta]]])
# assertNotAlmostEqual(first, second[, places[, msg[, delta]]])
# failIfAlmostEqual(first, second[, places[, msg[, delta]]])
# assertGreater(first, second, msg=None)
# assertGreaterEqual(first, second, msg=None)
# assertLess(first, second, msg=None)
# assertLessEqual(first, second, msg=None)
# assertMultiLineEqual(self, first, second, msg=None)
# assertRegexpMatches(text, regexp, msg=None)
# assertNotRegexpMatches(text, regexp, msg=None)
# assertIn(first, second, msg=None)
# assertNotIn(first, second, msg=None)
# assertItemsEqual(actual, expected, msg=None)
# assertSetEqual(set1, set2, msg=None)
# assertDictEqual(expected, actual, msg=None)
# assertDictContainsSubset(expected, actual, msg=None)
# assertListEqual(list1, list2, msg=None)
# assertTupleEqual(tuple1, tuple2, msg=None)
# assertSequenceEqual(seq1, seq2, msg=None, seq_type=None)
# assertRaises(exception[, callable, ...])
# failUnlessRaises(exception[, callable, ...])
# assertRaisesRegexp(exception, regexp[, callable, ...])
# assertIsNone(expr[, msg])
# assertIsNotNone(expr[, msg])
# assertIs(expr1, expr2[, msg])
# assertIsNot(expr1, expr2[, msg])
# assertIsInstance(obj, cls[, msg])
# assertNotIsInstance(obj, cls[, msg])
# assertFalse(expr[, msg])
# failIf(expr[, msg])
# fail([msg])
