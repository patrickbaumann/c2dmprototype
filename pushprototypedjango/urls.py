from django.conf.urls.defaults import *

# Uncomment the next two lines to enable the admin:
from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    # Example:
    # (r'^pushprototypedjango/', include('pushprototypedjango.foo.urls')),
    (r'^accounts/logout/$', 'push.views.logout_view'),
    (r'^accounts/login/$', 'push.views.login_view'),
    (r'^logout/$', 'push.views.logout'),
    (r'^admin/doc/', include('django.contrib.admindocs.urls')),
    (r'^admin/', include(admin.site.urls)),
    (r'^message/(\d+)/new','push.views.create_message'),
    (r'^(?:push/)?message/get(?:/(\d+))?', 'push.views.message_deliver'),
    (r'^(?:push/)?message/send/(\d+)', 'push.views.message_send'),
    (r'^(?:push/)?message/', 'push.views.message_recieve'),
    (r'^(?:push/)?register', 'push.views.register'),
    (r'^(?:push/)?list', 'push.views.list'),
    (r'^(\w+)/', 'push.views.show'),
    (r'^', 'push.views.list'),
)
