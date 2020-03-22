from django.shortcuts import render
import pymongo
from pymongo import MongoClient
from django.shortcuts import get_object_or_404, render, redirect
from pprint import pprint
import os, ssl

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

        if (not os.environ.get('PYTHONHTTPSVERIFY', '') and getattr(ssl, '_create_unverified_context', None)):
            ssl._create_default_https_context = ssl._create_unverified_context

        print( request.POST['username'] )
        client = connect()
        db = client.authorization.headquarters
        cursor = db.inventory.find({ 'username' :request.POST['username'] , 'password' : request.POST['password']})
        print ( cursor.count() )
        for inventory in cursor :
            pprint(inventory)
        data = { 'username' :request.POST['username'] , 'password' : request.POST['password']  }
        db.insert_one(data)


    return render(request, 'user/login_page.html')
