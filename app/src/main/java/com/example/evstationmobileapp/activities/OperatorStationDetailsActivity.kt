package com.example.evstationmobileapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.evstationmobileapp.R
import com.example.evstationmobileapp.models.*
import com.example.evstationmobileapp.remote.StationApiService
import com.example.evstationmobileapp.utils.SessionManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class OperatorStationDetailsActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private var stationId: String? = null
    private var station: Station? = null
    private var operators: List<Operator> = emptyList()
    private var slots: List<StationSlot> = emptyList()

    // UI Components
    private lateinit var btnBack: ImageView
    private lateinit var tvStationName: TextView
    private lateinit var tvStationId: TextView
    private lateinit var tvStationStatus: TextView
    private lateinit var tvStationType: TextView
    private lateinit var tvTotalSlots: TextView
    private lateinit var tvStationPhone: TextView
    private lateinit var tvFullLocation: TextView
    private lateinit var tvCoordinates: TextView
    private lateinit var tvOperatingHours: TextView
    private lateinit var operatorsContainer: LinearLayout
    private lateinit var slotsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operator_station_details)

        sessionManager = SessionManager(this)
        initializeViews()
        setupClickListeners()
        getStationIdFromIntent()
        loadStationDetails()
    }

    private fun initializeViews() {
        btnBack = findViewById(R.id.btnBack)
        tvStationName = findViewById(R.id.tvStationName)
        tvStationId = findViewById(R.id.tvStationId)
        tvStationStatus = findViewById(R.id.tvStationStatus)
        tvStationType = findViewById(R.id.tvStationType)
        tvTotalSlots = findViewById(R.id.tvTotalSlots)
        tvStationPhone = findViewById(R.id.tvStationPhone)
        tvFullLocation = findViewById(R.id.tvFullLocation)
        tvCoordinates = findViewById(R.id.tvCoordinates)
        tvOperatingHours = findViewById(R.id.tvOperatingHours)
        operatorsContainer = findViewById(R.id.operatorsContainer)
        slotsContainer = findViewById(R.id.slotsContainer)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun getStationIdFromIntent() {
        stationId = intent.getStringExtra("stationId")
        if (stationId == null) {
            Toast.makeText(this, "Station ID not found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadStationDetails() {
        stationId?.let { id ->
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = withContext(Dispatchers.IO) {
                        StationApiService.getStationById(id)
                    }
                    handleStationResponse(response)
                } catch (e: Exception) {
                    Toast.makeText(this@OperatorStationDetailsActivity, 
                        "Error loading station details: ${e.message}", 
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun handleStationResponse(response: String) {
        try {
            val jsonResponse = JSONObject(response)
            
            if (jsonResponse.has("error")) {
                val error = jsonResponse.getString("error")
                val details = jsonResponse.optString("details", "Unknown error")
                Toast.makeText(this, "$error: $details", Toast.LENGTH_LONG).show()
                return
            }

            // Parse station data
            val gson = Gson()
            station = gson.fromJson(response, Station::class.java)
            
            station?.let { stationData ->
                updateStationInfo(stationData)
                loadOperators()
                loadSlots()
            }

        } catch (e: Exception) {
            Toast.makeText(this, "Error parsing station data: ${e.message}", 
                Toast.LENGTH_LONG).show()
        }
    }

    private fun updateStationInfo(station: Station) {
        tvStationName.text = station.stationName
        tvStationId.text = "(${station.stationCode})"
        tvStationStatus.text = station.status
        tvStationType.text = station.stationType
        tvTotalSlots.text = station.noOfSlots.toString()
        tvStationPhone.text = station.phoneNumber
        tvFullLocation.text = station.location.address
        tvCoordinates.text = "Lat: ${station.location.latitude} | Lon: ${station.location.longitude}"
        
        val operatingHours = if (station.operatingHours.is24Hours) {
            "24/7"
        } else {
            "${station.operatingHours.openTime} - ${station.operatingHours.closeTime}"
        }
        tvOperatingHours.text = operatingHours

        // Update status badge color
        updateStatusBadge(station.status)
    }

    private fun updateStatusBadge(status: String) {
        val statusBackground = when (status.lowercase()) {
            "active" -> R.drawable.bg_status_confirmed
            "inactive" -> R.drawable.bg_status_cancelled
            else -> R.drawable.bg_status_pending
        }
        tvStationStatus.background = ContextCompat.getDrawable(this, statusBackground)
    }

    private fun loadOperators() {
        station?.operators?.let { operatorList ->
            operators = operatorList
            displayOperators(operatorList)
        }
    }

    private fun displayOperators(operatorList: List<Operator>) {
        operatorsContainer.removeAllViews()
        
        operatorList.forEach { operator ->
            val operatorView = createOperatorCard(operator)
            operatorsContainer.addView(operatorView)
        }
    }

    private fun createOperatorCard(operator: Operator): View {
        val inflater = LayoutInflater.from(this)
        val operatorView = inflater.inflate(R.layout.item_operator_card, operatorsContainer, false)
        
        val tvOperatorName = operatorView.findViewById<TextView>(R.id.tvOperatorName)
        val tvOperatorEmail = operatorView.findViewById<TextView>(R.id.tvOperatorEmail)
        val tvOperatorPhone = operatorView.findViewById<TextView>(R.id.tvOperatorPhone)
        
        tvOperatorName.text = "${operator.firstName} ${operator.lastName}"
        tvOperatorEmail.text = "Email: ${operator.email}"
        tvOperatorPhone.text = "Phone: ${operator.phoneNumber}"
        
        return operatorView
    }

    private fun loadSlots() {
        station?.slots?.let { slotList ->
            slots = slotList
            displaySlots(slotList)
        }
    }

    private fun displaySlots(slotList: List<StationSlot>) {
        slotsContainer.removeAllViews()
        
        slotList.forEach { slot ->
            val slotView = createSlotCard(slot)
            slotsContainer.addView(slotView)
        }
    }

    private fun createSlotCard(slot: StationSlot): View {
        val inflater = LayoutInflater.from(this)
        val slotView = inflater.inflate(R.layout.item_slot_card, slotsContainer, false)
        val slotCard = slotView.findViewById<CardView>(R.id.slotCard)
        val cardContentContainer = slotView.findViewById<LinearLayout>(R.id.cardContentContainer)
        val tvSlotId = slotView.findViewById<TextView>(R.id.tvSlotId)
        val tvConnectorType = slotView.findViewById<TextView>(R.id.tvConnectorType)
        val tvPower = slotView.findViewById<TextView>(R.id.tvPower)
        val tvPrice = slotView.findViewById<TextView>(R.id.tvPrice)
        val tvSlotStatus = slotView.findViewById<TextView>(R.id.tvSlotStatus)
        val statusBadgeContainer = slotView.findViewById<LinearLayout>(R.id.statusBadgeContainer)
        val ivStatusIcon = slotView.findViewById<ImageView>(R.id.ivStatusIcon)
        val ivStatusBadgeIcon = slotView.findViewById<ImageView>(R.id.ivStatusBadgeIcon)
        val btnEditStatus = slotView.findViewById<Button>(R.id.btnEditStatus)
        
        // Set slot details
        tvSlotId.text = slot.slotCode
        tvConnectorType.text = "Connector: ${slot.connectorType.toString()}"
        //tvPower.text = "Power: ${slot.powerRating.toString()}"
        val powerText = if (slot.powerRating != null) {
            // Access the 'value' property of the enum and append " kW"
            "${slot.powerRating.value} kW"
        } else {
            // Handle the case where the powerRating is null from the server
            "N/A"
        }
        tvPower.text = "Power: $powerText"

        tvPrice.text = "Price/kWh: Rs. ${slot.pricePerKWh.toInt()}"
        tvSlotStatus.text = slot.status.toString()
        
        // Update card appearance based on status
        updateSlotCardAppearance(slot, cardContentContainer, statusBadgeContainer, ivStatusIcon, ivStatusBadgeIcon)
        
        // Set up edit status button
        btnEditStatus.setOnClickListener {
            showStatusUpdateDialog(slot)
        }
        
        return slotView
    }

    private fun updateSlotCardAppearance(
        slot: StationSlot,
        cardContentContainer: LinearLayout, // <-- We now operate on the LinearLayout
        statusBadgeContainer: LinearLayout,
        ivStatusIcon: ImageView,
        ivStatusBadgeIcon: ImageView
    ) {
        val backgroundDrawable = when (slot.status) {
            SlotStatus.Available -> R.drawable.card_background_available
            SlotStatus.Occupied -> R.drawable.card_background_occupied
            SlotStatus.Maintenance -> R.drawable.card_background_maintenance
        }

        // This is the only line needed to change the appearance
        cardContentContainer.background = ContextCompat.getDrawable(this, backgroundDrawable)

        // The rest of your logic for the status icon remains the same
        when (slot.status) {
            SlotStatus.Available -> {
                statusBadgeContainer.background = ContextCompat.getDrawable(this, R.drawable.status_background_available)
                ivStatusIcon.setImageResource(R.drawable.ic_checkmark_circle)
                ivStatusIcon.setColorFilter(ContextCompat.getColor(this, R.color.green))
            }
            SlotStatus.Occupied -> {
                statusBadgeContainer.background = ContextCompat.getDrawable(this, R.drawable.status_background_occupied)
                ivStatusIcon.setImageResource(R.drawable.ic_checkmark_circle)
                ivStatusIcon.setColorFilter(ContextCompat.getColor(this, R.color.orange))
            }
            SlotStatus.Maintenance -> {
                statusBadgeContainer.background = ContextCompat.getDrawable(this, R.drawable.status_background_maintenance)
                ivStatusIcon.setImageResource(R.drawable.ic_wrench)
                ivStatusIcon.setColorFilter(ContextCompat.getColor(this, R.color.red))
            }
        }
    }

    private fun showStatusUpdateDialog(slot: StationSlot) {
        val statusOptions = arrayOf("Available", "Occupied", "Maintenance")
        val currentStatusIndex = statusOptions.indexOf(slot.status.toString())
        
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Update Slot Status")
            .setSingleChoiceItems(statusOptions, currentStatusIndex) { dialog, which ->
                val newStatus = statusOptions[which]
                if (newStatus != slot.status.toString()) {
                    updateSlotStatus(slot.id, newStatus)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun updateSlotStatus(slotId: String, newStatus: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    StationApiService.updateSlotAvailability(slotId, newStatus)
                }
                
                val jsonResponse = JSONObject(response)
                if (jsonResponse.has("error")) {
                    val error = jsonResponse.getString("error")
                    val details = jsonResponse.optString("details", "Unknown error")
                    Toast.makeText(this@OperatorStationDetailsActivity, 
                        "$error: $details", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@OperatorStationDetailsActivity, 
                        "Slot status updated successfully", Toast.LENGTH_SHORT).show()
                    // Reload station details to reflect changes
                    loadStationDetails()
                }
            } catch (e: Exception) {
                Toast.makeText(this@OperatorStationDetailsActivity, 
                    "Error updating slot status: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
