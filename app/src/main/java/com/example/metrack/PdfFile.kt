package com.example.metrack

data class PdfFile(val fileName : String , val downloadUrl : String){
    constructor() : this("","")
}