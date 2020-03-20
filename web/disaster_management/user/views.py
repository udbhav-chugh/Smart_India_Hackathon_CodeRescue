from django.shortcuts import render
import pymongo
from pymongo import MongoClient

def index (request):
    client = MongoClient('mongodb+srv://user:user@sih-jhvxc.mongodb.net/test?retryWrites=true&w=majority')
    db = client.database
    
    print("Success")
