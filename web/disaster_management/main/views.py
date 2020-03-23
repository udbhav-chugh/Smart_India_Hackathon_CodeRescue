from django.shortcuts import render
from django.http import HttpResponse, HttpResponseRedirect
from django.urls import reverse
import pymongo
from pymongo import MongoClient

def connect():
    client = MongoClient('mongodb+srv://coderescue:sih2020@trycluster-rfees.mongodb.net/test?retryWrites=true&w=majority&ssl=true&ssl_cert_reqs=CERT_NONE' , ssl = True)
    # client = MongoClient('mongodb+srv://user:user@sih-jhvxc.mongodb.net/test?retryWrites=true&w=majority')
    return client

def index(request):
    context = {}
    if request.session.has_key('location'):
        context['location'] = request.session['location']

    return render(request , 'main/index.html' , context)

def getUserLocation(request):
    if request.method == 'POST':
        location = request.POST['location']
        request.session['location'] = location
        return HttpResponseRedirect(reverse('main:index'))
#mayank code starts here... kindly accept my part of code if merge conflict arises

locations =["Andhra Pradesh","Arunachal Pradesh ","Assam","Bihar","Chhattisgarh","Goa","Gujarat",
"Haryana","Himachal Pradesh","Jammu and Kashmir","Jharkhand","Karnataka","Kerala",
"Madhya Pradesh","Maharashtra","Manipur","Meghalaya","Mizoram","Nagaland","Odisha",
"Punjab","Rajasthan","Sikkim","Tamil Nadu","Telangana","Tripura","Uttar Pradesh","Uttarakhand",
"West Bengal","Andaman and Nicobar Islands","Chandigarh","Dadra and Nagar Haveli","Daman and Diu",
"Lakshadweep","Delhi","Puducherry"]

def headquarters_dashboard(request):
    client = connect()
    db = client.main.disaster
    print("HELLO")
    info = db.find({})
    data = list(info)

    disaster_names = []
    location_names = []

    for data in data:
        disaster_names.append(data["name"])
    for data in disaster_names:
        print(data)

    for location in locations :
        location_names.append(location)

    return render( request , 'headquarters/dashboard.html' , {"disaster_names" : disaster_names , "location_names": location_names} )

def rescue_team_dashboard(request):
    return render( request , 'rescue_team/dashboard.html' )
