package com.example.whatstheweather

import com.google.firebase.firestore.PropertyName
import java.sql.Timestamp

data class SearchedData(val date:com.google.firebase.Timestamp=com.google.firebase.Timestamp.now(),val name:String = "")