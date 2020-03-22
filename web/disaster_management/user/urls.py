from django.urls import path,include
from . import views

app_name = "leave_portal"
urlpatterns = [
    path('login/',views.login , name="login"),


]
