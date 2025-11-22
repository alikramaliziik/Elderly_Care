package old.people.elderly_care.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "caregivers")
data class Caregiver(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    @ColumnInfo(name = "full_name")
    val fullName: String,
    
    val email: String,
    
    @ColumnInfo(name = "password_hash")
    val passwordHash: String,
   
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    val phone: String,
    val age: Int,
    val gender: String,
    
    @ColumnInfo(name = "profile_picture")
    val profilePicture: ByteArray? = null  
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Caregiver

        if (id != other.id) return false
        if (fullName != other.fullName) return false
        if (email != other.email) return false
        if (passwordHash != other.passwordHash) return false
        if (phone != other.phone) return false
        if (age != other.age) return false
        if (gender != other.gender) return false
        if (profilePicture != null) {
            if (other.profilePicture == null) return false
            if (!profilePicture.contentEquals(other.profilePicture)) return false
        } else if (other.profilePicture != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + fullName.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + passwordHash.hashCode()
        result = 31 * result + phone.hashCode()
        result = 31 * result + age
        result = 31 * result + gender.hashCode()
        result = 31 * result + (profilePicture?.contentHashCode() ?: 0)
        return result
    }
}
