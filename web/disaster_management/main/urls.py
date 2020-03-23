from django.urls import path
from . import views

app_name = 'main'

urlpatterns = [
    path('', views.index, name='index'),
    path('getlocation', views.getUserLocation, name = 'getUserLocation'),
    path('headquarters_dashboard' , views.headquarters_dashboard , name='headquarters_dashboard'),
    path('rescue_team_dashboard' , views.rescue_team_dashboard , name='rescue_team_dashboard')
]
