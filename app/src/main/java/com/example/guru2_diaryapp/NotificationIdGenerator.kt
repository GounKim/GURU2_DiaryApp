
package com.example.guru2_diaryapp
import java.util.concurrent.atomic.AtomicInteger

class NotificationIdGenerator {
    private val id: AtomicInteger = AtomicInteger(0)

    fun getID(): Int {
        return id.incrementAndGet()
    }
}