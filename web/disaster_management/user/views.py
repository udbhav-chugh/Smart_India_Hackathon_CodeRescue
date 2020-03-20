from django.shortcuts import render
import pymongo
from pymongo import MongoClient

def index (request):
    client = MongoClient('mongodb+srv://code_rescue:sih2020@sih-jhvxc.mongodb.net/test?retryWrites=true&w=majority')

    print("Success")
    
