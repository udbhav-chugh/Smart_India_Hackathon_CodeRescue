from django.shortcuts import render
from django.http import HttpResponse, HttpResponseRedirect, JsonResponse
from django.core import serializers
from django.urls import reverse
import pymongo
from pymongo import MongoClient
from datetime import datetime
from graphos.sources.simple import SimpleDataSource
from graphos.renderers.gchart import LineChart
from django.template.loader import render_to_string

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
    client = connect()

    if request.session.has_key('locationIndex'):
        context['locationIndex'] = request.session['locationIndex']
        print(context['locationIndex'])
        context['locationName'] = request.session['locationName']

    location_names = []
    for location in locations :
        location_names.append(location)
    context['location_names'] = location_names

    db = client.main.disaster
    print("HELLO Main Dashboard")

    info = db.find({})
    temp_data = list(info)

# CREATING A DICTIONARY OF ALL DISASTER RELATED DATA
    data = {}
    for disaster in temp_data:
        if "name" in disaster:
            data[disaster["name"]] = disaster

# #WORKING ON CHARTS!!
#
#     charts_data = {}
#
#     for data in temp_data:
#         chart = [['Day' , 'Affected' , 'Deaths']]
#
#
#
#
#
#
#     data =  [
#             ['Year', 'Sales', 'Expenses'],
#             [2004, 1000, 400],
#             [2005, 1170, 460],
#             [2006, 660, 1120],
#             [2007, 1030, 540]
#         ]
#     data_source = SimpleDataSource(data=data)
#     chart = LineChart(data_source)
#     context = {'chart': chart}
#     return render(request, 'yourtemplate.html', context)
#


    context['data'] = data
    return render(request , 'main/index.html' , context)

def getUserLocation(request):
    if request.method == 'POST':
        locName = request.POST.get('location')
        # location = location.tolower()
        if locName in locations:
            request.session['locationName'] = locName
            request.session['locationIndex'] = locations.index(locName)
    return HttpResponseRedirect(reverse('main:index'))

def notifications(request, loc_no):
    client = connect()
    db = client.main.notification
    print("connected")
    data = db.find().sort("date", pymongo.DESCENDING)
    allnotfs = list(data)
    if 0 <= loc_no < len(locations):
        notfLocation = locations[loc_no]
    else:
        HttpResponseRedirect(reverse('main:index'))
    ########################
    # some error chances bcoz i m considering last time of
    # or may be not
    print(allnotfs)
    notfs = []
    for notf in allnotfs:
        if 'location' in notf and notf['location'] == notfLocation:
            notfs.append(notf)

    if notfs != []:
        request.session['lastNotification'] = notfs[0]['date']
        print(notfs[0]['date'])
    context = {
        'notifications' : notfs,
        'notfLocIndex' : loc_no
    }
    return render(request , 'main/notification.html' , context)

def get_new_notifications(request, loc_no):
    if request.is_ajax and request.method == "GET":
        lastNotif = request.session['lastNotification']
        # get locName from url and not session
        locName = locations[loc_no]
        client = connect()
        db = client.main.notification
        print("Queried new notifications")
        data = db.find().sort("date", pymongo.DESCENDING)
        allnotfs = list(data)
        # print(allnotfs)
        # print(lastNotif)
        newnotfs = []
        for notf in allnotfs:
            if 'location' in notf and notf['location'] == locName:
                if notf['date'] != lastNotif:
                    ########### since ObjectId is not json serializable
                    notf['_id'] = 0
                    newnotfs.append(notf)
                else:
                    break
        if newnotfs != []:
            request.session['lastNotification'] = newnotfs[0]['date']
            newnotfs.reverse()
        # so that last notification is picked first to add
        # print(newnotfs)
        return JsonResponse({"new_notifications": newnotfs}, status=200)
    else:
        HttpResponseRedirect(reverse('main:index'))
        ############ change this

#mayank code starts here... kindly accept my part of code if merge conflict arises

def headquarters_dashboard(request):
    client = connect()
    success = 0
    dt_string = datetime.now()

    if( request.method == 'POST' ):
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
    active_disasters = []
    for data1 in data:
        disaster_names.append(data1["name"])
        if data1['isactive'] == 1:
            active_disasters.append(data1)

    for data1 in data :
        rescue_teams_names[data1["name"]] = data1["rescue_teams_usernames"]

    for location in locations :
        location_names.append(location)

    # print(active_disasters)
    context = {
        "disaster_names" : disaster_names ,
        "location_names": location_names ,
        "success" : success ,
        "rescue_teams_names" : rescue_teams_names,
        "active_disasters" : active_disasters
    }

    return render( request , 'headquarters/dashboard.html' , context )

def rescue_team_dashboard(request):
    return render( request , 'rescue_team/dashboard.html' )

def all_disasters(request):
    client = connect()
    db = client.main.disaster
    print("Connected")
    info = db.find({})
    data = list(info)

    disasters_data = []
    for record in data:
        temp = {}
        temp['id'] = record['id']
        temp['name'] = record['name']
        temp['location'] = record['location']
        temp['isactive'] = record['isactive']
        disasters_data.append(temp)

    context = {
        'disasters_data' : disasters_data
    }
    return render(request, 'headquarters/disasters.html', context)

def change_active_status(request):
    if request.is_ajax and request.method == "POST":
        data = request.POST
        status = int(data['status'])
        id = data['id']
        print(id + " " + str(status))
        client = connect()
        db = client.main.disaster
        print("Connected")
        db.update_one(
        { "id" : id },
        { "$set": { "isactive" : status } }
        )
        return JsonResponse({}, status=200)
    return JsonResponse({"error": "some error"}, status=400)

def add_disaster(request):
    if request.method == "GET":
        return render(request, 'headquarters/add_disaster.html')

    elif request.method == "POST":
        print("From received")
        client = connect()
        db = client.main.disaster
        id = db.count() + 1
        location = []
        for loc in request.POST.getlist('location'):
            if loc != '':
                location.append(loc)

        data = {
            'id' : "unique_id_" + str(id),
            'name' : request.POST['name'],
            'isactive' : int(request.POST['activeStatus']),
            'scale' : int(request.POST['scale']),
            'coordinates' : {
                'latitude' : request.POST['latitude'],
                'longitude' : request.POST['longitude'],
                'radius' : request.POST['radius']
            },
            'rescue_teams_usernames' : [],
            'statistics' : {
                'total' : {
                    'affected' : 0,
                    'deaths' : 0
                }
            },
            'location' : location
        }
        print(data)
        db.insert_one(data)
        return HttpResponseRedirect(reverse('main:all_disasters'))
