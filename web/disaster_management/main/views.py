from django.shortcuts import render
from django.http import HttpResponse, HttpResponseRedirect
from django.urls import reverse

def index(request):
    context = {}
    return render(request , 'main/index.html' , context)
