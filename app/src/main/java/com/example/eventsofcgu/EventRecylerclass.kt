package com.example.eventsofcgu

import com.google.firebase.database.PropertyName

data class EventRecylerclass( var club: String?=null,var event: String?=null,var eventdesc: String? = null,var image: String? = null, var registeredusers: List<String>? = null ,var reglink: String? = null )

