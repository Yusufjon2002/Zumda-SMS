package com.mstar004.models

import java.io.Serializable

data class ContactList(
    val contacts: List<Contacts>
) : Serializable
