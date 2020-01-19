# CarParked

Tag where you've parked and easily find it later with easy to follow walking directions. No more wandering the streets, wondering where you parked your car. 

'Carparked' was built as part of a week 'individual project' as park of the [Victoria University of Wellington, Master of Software Development](https://www.wgtn.ac.nz/ecs/study/postgraduate-study/software-development) Course. The aim of the project is to 'impress' faculty by building a novel piece of software using new skills learnt through the week and building on skills taught over the course of the program. 

## Getting Started

These instructions will get you a copy of the project up and running on your android phone or emulator for development and testing purposes. 

### Prerequisites

* Android 6.0 or above
* Mapbox API Key. Available for free (25,000 users and 100,000 requests) - https://www.mapbox.com/


### Installing

1. Update "access_token" with your API Key. 
```
app > re > values > strings.xml
<string name="access_token">//@TODO ADD YOUR OWN API KEY FOR MAPBOX</string>
```
2. Build Signed API
3. Install on Device and run. 

## How to Use

### Tag Car Location
Click Red *PARK HERE* Button

![Before Car Parked](/parkHereBefore.PNG)
![After Car Parked](/parkHereAfter.PNG)

### Get Directions to CarPark
Click Green *FIND LAST PARK* Button

![Before Getting Directions](/findParkBefore.PNG)
![After Getting Directions](/findParkAfter.PNG)

### ReCentre Camera
Click Compass; top left.

![Camera not centred on user location](/centreBefore.PNG)
![Camera centred on user location](/centreAfter.PNG)

## Testing

Integration and User Tested on: 

* Google Pixel 2 - Emulated
* Huawei P9 Lite
* Xiaomi Mi 9T

## Future Releases

* Historical carpark view
* Directions to historical parks
* Integration with Flamingo Scooters, JUMP Scooters and [OnzO](https://github.com/ubahnverleih/WoBike/blob/master/Onzo.md)

## Built With

* [Kotlin](https://kotlinlang.org/) - Programming Language
* [MapBox](https://www.mapbox.com/) - Mapping Software
* [SQLite](https://www.sqlite.org/index.html) - Local Database Tool
* [AdMob](https://admob.google.com/home/) - Mobile Advertising
* [Android Studio](https://developer.android.com/studio) - Official Android IDE
* [Scrum](https://en.wikipedia.org/wiki/Scrum_(software_development)) - Workflow Methodology

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.


## Authors

* **Sam Lock**  - [Website](http://samlock.nz)


## Acknowledgments

* [Joshua La Pine](https://github.com/Jelop) for additional user testing
* [Ali Ahmed](https://ecs.wgtn.ac.nz/Main/AliAhmed) for assistance with SQLite set up
* [Devslopes](https://devslopes.com/) for excellent mapbox video tutorials in Kotlin
* CarParked icon made by Freepik from www.flaticon.com
* Anyone who posted and answered in the many StackOverflow Questions I have read in the course of this assignment

