package com.bookshelfhub.core.model.entities.remote

class RemoteCollaborator(
   val collabName: String,
   val collabId: String,
   val bookName: String,
   val dateCreated: Any,
   val pubName: String,
   val bookId: String,
   val collabCommissionInPercent: Double,
   val link: String,
   val collabEmail: String,
   val totalEarningsInNGN: Double,
   val totalEarningsInUSD: Double
)