import geocoder
from geopy.geocoders import Nominatim
import requests
import json
import time

api_key = "178253a63e78c4c039753978d469157f"  # open weather

while(True):
    g = geocoder.ip('me')
    # print(g.latlng)
    lat = g.latlng[0]
    lon = g.latlng[1]

    geolocator = Nominatim(user_agent="view-magnet")
    location = geolocator.reverse(str(lat)+","+str(lon))
    # print(location)

    # open weather
    url = "https://api.openweathermap.org/data/2.5/weather?&lat=" + \
        str(lat) + "&lon=" + str(lon) + "&units=metric" + "&APPID=" + api_key

    x = requests.post(url)
    data = x.json()
    print(str(data).replace("'", "\""), flush= True)
    time.sleep(3600)
