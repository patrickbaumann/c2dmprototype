from django.conf.urls.defaults import *

# Uncomment the next two lines to enable the admin:
from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    # Example:
    # (r'^pushprototypedjango/', include('pushprototypedjango.foo.urls')),

    (r'^admin/doc/', include('django.contrib.admindocs.urls')),
    (r'^admin/', include(admin.site.urls)),
    (r'^message/(\d+)/new','push.views.create_message'),
    (r'^(?:push/)?message/', 'push.views.message_recieve'),
    (r'^(?:push/)?register', 'push.views.register'),
    (r'^(\w+)/', 'push.views.show'),
    (r'^', 'push.views.list'),
    
)
