from django.shortcuts import render
import pymongo
from pymongo import MongoClient
from django.shortcuts import get_object_or_404, render, redirect
from pprint import pprint
import os, ssl
import main

def connect():
    client = MongoClient('mongodb+srv://coderescue:sih2020@trycluster-rfees.mongodb.net/test?retryWrites=true&w=majority&ssl=true&ssl_cert_reqs=CERT_NONE' , ssl = True)
    # client = MongoClient('mongodb+srv://user:user@sih-jhvxc.mongodb.net/test?retryWrites=true&w=majority')
    return client

def index (request):
    client = MongoClient('mongodb+srv://user:user@sih-jhvxc.mongodb.net/test?retryWrites=true&w=majority')
    db = client.database
    print("Success")

def login (request):

    if( request.method == 'POST' ):

        print( request.POST['username'] )
        client = connect()

        # if headquarters is trying to login
        db = client.authorization.headquarters

        cursor = db.find({})
        # pprint(cursor)
        # print(cursor)
        data = list(cursor)
        for data in data:
            print(data['username'] + "'s password is' " + data['password']  )


        count_authorities = db.count_documents({ 'username' :request.POST['username'] , 'password' : request.POST['password']  })
        print(count_authorities)
        if count_authorities == 1:
            return redirect( 'main:headquarters_dashboard' )

        # if rescue team is trying to log in
        db = client.authorization.rescue_team
        count_authorities = db.count_documents({ 'username' :request.POST['username'] , 'password' : request.POST['password']  })
        print(count_authorities)
        if count_authorities == 1:
            return redirect( 'main:rescue_team_dashboard' )

        return render( request , 'user/login_page.html' , {"error" : 1})
    return render(request, 'user/login_page.html')
