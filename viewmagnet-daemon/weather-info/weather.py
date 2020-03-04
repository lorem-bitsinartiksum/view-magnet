import geocoder
from geopy.geocoders import Nominatim
import requests
import json

# api_key = "c28ebd4d55624ac7b5d21791b3d3be22" # weatherbit
api_key = "178253a63e78c4c039753978d469157f"  # open weather

g = geocoder.ip('me')
# print(g.latlng)
lat = g.latlng[0]
lon = g.latlng[1]

geolocator = Nominatim(user_agent="view-magnet")
location = geolocator.reverse(str(lat)+","+str(lon))
# print(location)

# weatherbit
#url = "https://api.weatherbit.io/v2.0/current?&lat=" +str(lat) + "&lon=" + str(lon) + "&key=" + api_key

# open weather
url = "https://api.openweathermap.org/data/2.5/weather?&lat=" + \
    str(lat) + "&lon=" + str(lon) + "&units=metric" + "&APPID=" + api_key

x = requests.post(url)
# print(x.text)
data = x.json()
print(str(data).replace("'", "\""), flush= True)

# open weather
# print("name: " + str(data["name"]))
# print("temp: " + str(data["main"]["temp"]))
# print("feels_like: " + str(data["main"]["feels_like"]))
# print("temp_min: " + str(data["main"]["temp_min"]))
# print("temp_max: " + str(data["main"]["temp_max"]))
# print("weather: " + str(data["weather"][0]["main"]))
# print("wind_spd: " + str(data["wind"]["speed"]))
# timezone = data["timezone"]
# sunrise = data["sys"]["sunrise"] + timezone
# sunset = data["sys"]["sunset"] + timezone
# print("sunrise: " + str(sunrise))
# print("sunset: " + str(sunset))
# print("timezone: " + str(data["timezone"]))
# print("country: " + data["sys"]["country"])
