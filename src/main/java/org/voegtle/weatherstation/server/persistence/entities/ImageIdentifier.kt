package org.voegtle.weatherstation.server.persistence.entities

import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id
import org.voegtle.weatherstation.server.util.StringUtil.isEmpty

@Entity
class ImageIdentifier {
    @Id
    var id: Long? = null
    var sheet: Int? = null
    var oid: String? = null
    var zx: String? = null
    var format: String = FORMAT_IMAGE

    constructor()

    constructor(sheet: Int?, oid: String?) {
        this.sheet = sheet
        this.oid = oid
    }

    fun asUrlParameter(): String {
        return if (isEmpty(zx)) {
            "?oid=$oid&format=$format"
        } else {
            "&oid=$oid&zx=$zx"
        }
    }

    companion object {
        const val FORMAT_IMAGE = "image"
    }
}
