from django.shortcuts import render
from django.http import HttpResponse, HttpResponseRedirect, JsonResponse
from django.core import serializers
from django.urls import reverse
import pymongo
from pymongo import MongoClient
from datetime import datetime

locations = ["Andhra Pradesh","Arunachal Pradesh ","Assam","Bihar","Chhattisgarh","Goa","Gujarat",
"Haryana","Himachal Pradesh","Jammu and Kashmir","Jharkhand","Karnataka","Kerala",
"Madhya Pradesh","Maharashtra","Manipur","Meghalaya","Mizoram","Nagaland","Odisha",
"Punjab","Rajasthan","Sikkim","Tamil Nadu","Telangana","Tripura","Uttar Pradesh","Uttarakhand",
"West Bengal","Andaman and Nicobar Islands","Chandigarh","Dadra and Nagar Haveli","Daman and Diu",
"Lakshadweep","Delhi","Puducherry"]

def connect():
    client = MongoClient('mongodb+srv://coderescue:sih2020@trycluster-rfees.mongodb.net/test?retryWrites=true&w=majority&ssl=true&ssl_cert_reqs=CERT_NONE' , ssl = True)
    # client = MongoClient('mongodb+srv://user:user@sih-jhvxc.mongodb.net/test?retryWrites=true&w=majority')
    return client

def index(request):
    context = {}
    if request.session.has_key('location'):
        context['location'] = request.session['location']
        print(context['location'])
    return render(request , 'main/index.html' , context)

def getUserLocation(request):
    if request.method == 'POST':
        location = request.POST['location']
        # location = location.tolower()
        if location in locations:
            request.session['location'] = location
    return HttpResponseRedirect(reverse('main:index'))

def notifications(request, loc):
    client = connect()
    db = client.main.notification
    print("connected")
    data = db.find().sort("date", pymongo.DESCENDING)
    notfs = list(data)
    ########################
    # some error chances bcoz i m considering last time of
    # or may be not
    request.session['lastNotification'] = notfs[0]['date']
    print(notfs[0]['date'])
    context = {
        'notifications' : notfs
    }
    return render(request , 'main/notification.html' , context)

def get_new_notifications(request):
    if request.is_ajax and request.method == "GET":
        lastNotif = request.session['lastNotification']
        client = connect()
        db = client.main.notification
        print("Queried new notifications")
        data = db.find().sort("date", pymongo.DESCENDING)
        notfs = list(data)
        newnotfs = []
        for notf in notfs:
            if notf['date'] != lastNotif:
                ########### since ObjectId is not json serializable
                notf['_id'] = 0
                newnotfs.append(notf)
            else:
                break
        request.session['lastNotification'] = notfs[0]['date']
        newnotfs.reverse()
        # so that last notification is picked first to add
        return JsonResponse({"new_notifications": newnotfs}, status=200)
    else:
        HttpResponseRedirect(reverse('main:index'))
        ############ change this

#mayank code starts here... kindly accept my part of code if merge conflict arises

def headquarters_dashboard(request):
    client = connect()
    success = 0
    dt_string = datetime.now().strftime("%d/%m/%Y %H:%M:%S")

    if( request.method == 'POST' ):
        print( request.POST['is_disaster'] )
        print( request.POST['disaster_names'] )
        print( request.POST['location_names'] )
        print( request.POST['message'] )

        if( request.POST['is_disaster'] == "disaster_wise" ):
            data = {
                "is_disaster" :  1,
                "name" : request.POST['disaster_names'],
                "directed_to" : "people",
                "directed_from" : "headquarters",
                "message" : request.POST['message'],
                "date" : dt_string
            }
            db = client.main.notification
            db.insert_one(data)
            success = 1

        if( request.POST['is_disaster'] == "location_wise" ):
            data = {
                "is_disaster" :  0,
                "location" : request.POST['location_names'],
                "directed_to" : "people",
                "directed_from" : "headquarters",
                "message" : request.POST['message'],
                "date" : dt_string
            }
            db = client.main.notification
            db.insert_one(data)
            success = 1

    db = client.main.disaster
    print("HELLO")
    info = db.find({})
    data = list(info)

    disaster_names = []
    location_names = []
    rescue_teams_names = {}
    for data1 in data:
        disaster_names.append(data1["name"])

    for data1 in data :
        rescue_teams_names[data1["name"]] = data1["rescue_teams_usernames"]

    for location in locations :
        location_names.append(location)

    return render( request , 'headquarters/dashboard.html' , {"disaster_names" : disaster_names , "location_names": location_names , "success" : success , "rescue_teams_names" : rescue_teams_names } )

def rescue_team_dashboard(request):
    return render( request , 'rescue_team/dashboard.html' )
