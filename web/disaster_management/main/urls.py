from django.urls import path
from . import views

app_name = 'main'

urlpatterns = [
    path('', views.index, name='index'),
    path('getlocation', views.getUserLocation, name = 'getUserLocation'),
    path('get/notifications/<int:loc_no>', views.notifications, name = 'notifications'),
    path('headquarters_dashboard' , views.headquarters_dashboard , name='headquarters_dashboard'),
    path('headquarters/dashboard/add_disaster', views.add_disaster, name = 'add_disaster'),
    path('headquarters/dashboard/all_disasters', views.all_disasters, name = 'all_disasters'),
    path('headquarters/dashboard/all_disasters/change_active_status', views.change_active_status, name='change_active_status'),
    path('headquarters/account/logout', views.headquartersLogout, name = 'logout'),
    path('headquarters/dashboard/send_notification', views.send_notification, name = 'send_notification'),
    path('headquarters/dashboard/disaster/<str:disaster_id>/update_statistics', views.update_statistics, name='update_statistics'),
    path('get/ajax/notifications/new/<int:loc_no>', views.get_new_notifications, name = 'get_new_notifications'),
    path('headquarters/dashboard/add_safe_house', views.add_safe_house, name = 'add_safe_house'),
    path('headquarters/dashboard/add_rescue_team', views.add_rescue_team, name = 'add_rescue_team'),
    path('<str:latitude>/<str:longitude>/<str:cityUser>', views.index, name = 'find_safe_house'),
]
