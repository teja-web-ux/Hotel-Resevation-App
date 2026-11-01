package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.HotelRepository
import com.example.model.PaymentStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Star Hotel", appName)
  }

  @Test
  fun `verify real-time payment settles directly to admin bank account`() {
    val room = HotelRepository.rooms.value.first()
    val initialBalance = HotelRepository.bankDetails.value.currentBalance

    val booking = HotelRepository.createBooking(
      guestName = "Test Guest",
      guestEmail = "test@guest.com",
      guestPhone = "+91 99999 88888",
      room = room,
      checkInDate = "2026-11-15",
      checkOutDate = "2026-11-16",
      nights = 1,
      guestsCount = 2,
      specialRequests = "None"
    )

    assertNotNull(booking)
    assertEquals(PaymentStatus.PENDING, booking.paymentStatus)

    val updatedBooking = HotelRepository.processPayment(booking.id, "UPI")
    assertNotNull(updatedBooking)
    assertEquals(PaymentStatus.PAID, updatedBooking?.paymentStatus)

    val newBalance = HotelRepository.bankDetails.value.currentBalance
    assertEquals(initialBalance + booking.totalAmount, newBalance, 0.01)
  }

  @Test
  fun `verify phone number SMS OTP dispatch and verification`() {
    val testPhone = "9876543210"
    val formattedPhone = "+91 9876543210"

    // 1. Send OTP to phone
    val otpCode = HotelRepository.sendOtp(testPhone)
    assertNotNull(otpCode)
    assertEquals(6, otpCode.length)

    // 2. Reject incorrect OTP
    val invalidResult = HotelRepository.verifyOtp(testPhone, "000000")
    org.junit.Assert.assertFalse(invalidResult)

    // 3. Verify with normalized phone (+91 variant) and generated OTP
    val validResult = HotelRepository.verifyOtp(formattedPhone, otpCode)
    assertTrue(validResult)
  }
}
