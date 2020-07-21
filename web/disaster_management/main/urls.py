from django.urls import path
from . import views

app_name = 'main'

urlpatterns = [
    path('', views.index, name='index'),
    path('getlocation', views.getUserLocation, name = 'getUserLocation'),
    path('notifications/<int:loc_no>', views.notifications, name = 'notifications'),
    path('get/ajax/notifications/new/<int:loc_no>', views.get_new_notifications, name = 'get_new_notifications'),
    path('headquarters_dashboard' , views.headquarters_dashboard , name='headquarters_dashboard'),
    path('rescue_team_dashboard' , views.rescue_team_dashboard , name='rescue_team_dashboard'),
    path('headquarters/add_disaster', views.add_disaster, name = 'add_disaster'),
    path('headquarters/all_disasters', views.all_disasters, name = 'all_disasters'),
    path('headquarters/all_disasters/change_active_status', views.change_active_status, name='change_active_status')
]
