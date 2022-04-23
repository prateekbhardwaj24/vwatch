package com.example.videostreamingapp.firebase

import com.example.videostreamingapp.bottomnavfragment.home.privateromm.PrivateVRoomFragment
import com.example.videostreamingapp.bottomnavfragment.home.publicroom.PublicVRoomFragment
import com.example.videostreamingapp.firebaseDatabaseService
import com.example.videostreamingapp.ui.RoomActivity
import com.google.firebase.database.Query

class HomeFirebaseDao() {

    fun getAllPublicVRooms(key: String?):Query{
        if (key == null){
            return firebaseDatabaseService.ref.child(firebaseDatabaseService.PUBLIC_ROOMS).orderByKey()
                .limitToFirst(RoomActivity.ITEM_COUNT)
        }
        return firebaseDatabaseService.ref.child(firebaseDatabaseService.PUBLIC_ROOMS).orderByKey().startAfter(key)
            .limitToFirst(RoomActivity.ITEM_COUNT)
    }

    fun getAllPrivateVRooms(key: String?):Query{
        if (key == null){
            return firebaseDatabaseService.ref.child(firebaseDatabaseService.PRIVATE_ROOM)
                .child(firebaseDatabaseService.firebaseAuth.currentUser!!.uid)
                .child(firebaseDatabaseService.ALL_ROOMS).orderByKey()
                .limitToFirst(RoomActivity.ITEM_COUNT)
        }
        return firebaseDatabaseService.ref.child(firebaseDatabaseService.PRIVATE_ROOM)
            .child(firebaseDatabaseService.firebaseAuth.currentUser!!.uid)
            .child(firebaseDatabaseService.ALL_ROOMS).orderByKey().startAfter(key)
            .limitToFirst(RoomActivity.ITEM_COUNT)
    }

    fun getAllOnlineViewers(key: String?, roomId: String): Query {
        if (key == null) {
            return firebaseDatabaseService.musicRef.child(firebaseDatabaseService.CURRENT_PLAYER)
                .child(roomId)
                .child(firebaseDatabaseService.ONLINE_VIEWERS).orderByKey()
                .limitToFirst(RoomActivity.ITEM_COUNT)
        }
        return firebaseDatabaseService.musicRef.child(firebaseDatabaseService.CURRENT_PLAYER).child(roomId)
            .child(firebaseDatabaseService.ONLINE_VIEWERS).orderByKey().startAfter(key)
            .limitToFirst(RoomActivity.ITEM_COUNT)
    }

    fun getAllOnlineViewersOfVideo(key: String?, roomId: String): Query {
        if (key == null) {
            return firebaseDatabaseService.ref.child(firebaseDatabaseService.CURRENT_ROOM).child(roomId)
                .child(firebaseDatabaseService.ONLINE_VIEWERS).orderByKey().limitToFirst(RoomActivity.ITEM_COUNT)
        }
        return firebaseDatabaseService.ref.child(firebaseDatabaseService.CURRENT_ROOM).child(roomId)
            .child(firebaseDatabaseService.ONLINE_VIEWERS).orderByKey().startAfter(key)
            .limitToFirst(RoomActivity.ITEM_COUNT)
    }

    fun getAllPublicAudioRoom(key: String?): Query {
        if (key == null) {
            return firebaseDatabaseService.musicRef
                .child(firebaseDatabaseService.PUBLIC_ROOMS).orderByKey().limitToFirst(RoomActivity.ITEM_COUNT)
        }
        return firebaseDatabaseService.musicRef
            .child(firebaseDatabaseService.PUBLIC_ROOMS).orderByKey().startAfter(key).limitToFirst(RoomActivity.ITEM_COUNT)
    }

    fun getAllPrivateAudioRoom(key: String?): Query {
        if (key == null) {
            return firebaseDatabaseService.musicRef
                .child(firebaseDatabaseService.PRIVATE_ROOM)
                .child(firebaseDatabaseService.firebaseAuth.currentUser!!.uid).child(firebaseDatabaseService.ALL_ROOMS)
                .orderByKey().limitToFirst(RoomActivity.ITEM_COUNT)
        }
        return firebaseDatabaseService.musicRef
            .child(firebaseDatabaseService.PRIVATE_ROOM)
            .child(firebaseDatabaseService.firebaseAuth.currentUser!!.uid).child(firebaseDatabaseService.ALL_ROOMS)
            .orderByKey().startAfter(key).limitToFirst(RoomActivity.ITEM_COUNT)
    }

    fun getSongsId(key: String?): Query {
        if (key == null) {
            return firebaseDatabaseService.musicRef
                .child(firebaseDatabaseService.MUSIC_FILES)
                .child(firebaseDatabaseService.firebaseAuth.currentUser!!.uid).child(firebaseDatabaseService.ALL_SONGS)
                .orderByKey().limitToFirst(RoomActivity.ITEM_COUNT)
        }
        return firebaseDatabaseService.musicRef
            .child(firebaseDatabaseService.MUSIC_FILES)
            .child(firebaseDatabaseService.firebaseAuth.currentUser!!.uid).child(firebaseDatabaseService.ALL_SONGS).orderByKey()
            .startAfter(key).limitToFirst(RoomActivity.ITEM_COUNT)
    }
}