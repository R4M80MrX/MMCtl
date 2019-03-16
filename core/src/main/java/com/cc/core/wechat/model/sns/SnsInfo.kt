package com.cc.core.wechat.model.sns

class SnsInfo {
    companion object {
        val TEXT_TYPE = 0
        val IMAGE_TYPE = 1
        val VIDEO_TYPE = 2
        val CARD_TYPE = 3
    }

    private var description: String? = null
    private var medias: ArrayList<String>? = null
    private var snsType: Int = 0
    private var url : String? = null
    private var shareTitle: String? = null

    fun getSnsType(): Int {
        return snsType
    }

    fun setSnsType(type: Int) {
        this.snsType = type
    }

    fun getDescription(): String? {
        return description
    }

    fun setDescription(desc: String?) {
        this.description = desc
    }

    fun getMedias(): ArrayList<String>? {
        return medias
    }

    fun setMedias(media: ArrayList<String>?) {
        this.medias = media
    }

    fun getUrl() : String? {
        return url
    }

    fun setUrl(url : String?) {
        this.url = url
    }

    fun getShareTitle() : String? {
        return shareTitle
    }

    fun setShareTitle(title : String?) {
        this.shareTitle = title
    }
}