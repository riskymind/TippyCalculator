package com.example.tippycal

import android.animation.ArgbEvaluator
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.core.content.ContextCompat
import com.example.tippycal.Constants.INITIAL_TIP_PERCENT
import com.example.tippycal.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.apply {
            sbPercent.progress = INITIAL_TIP_PERCENT
            tvTipPercentLabel.text = getString(R.string.tip_percent_label_text, INITIAL_TIP_PERCENT)
        }

        updateTipDescription(INITIAL_TIP_PERCENT)

        binding.etBaseAmount.addTextChangedListener(editTextChange)
        binding.sbPercent.setOnSeekBarChangeListener(seekBarProgress)
        binding.signature.setOnClickListener {
            gotoGitHub()
        }
    }

    private fun gotoGitHub() {
        val gitLink = "https://github.com/riskymind"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(gitLink))
        startActivity(intent)
    }


    private fun updateTipDescription(tipPercent: Int) {
        val desc = when (tipPercent) {
            in 0..9 -> "Poor"
            in 10..14 -> "Acceptable"
            in 15..19 -> "Good"
            in 20..24 -> "Great"
            else -> "Amazing"
        }

        val color = ArgbEvaluator().evaluate(
            tipPercent / binding.sbPercent.max.toFloat(),
            ContextCompat.getColor(this, R.color.color_worst_tip),
            ContextCompat.getColor(this, R.color.color_best_tip)
        ) as Int

        binding.apply {
            tvTipDesc.text = desc
            tvTipDesc.setTextColor(color)
        }
    }

    private val editTextChange = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            computeTipAndTotalAmount()
        }

    }

    private fun computeTipAndTotalAmount() {
        // validate the input field
        if (binding.etBaseAmount.text.isEmpty()) {
            binding.apply {
                tvTotalAmount.text = ""
                tvTipAmount.text = ""
            }
            return
        }

        // Get the input and tip percent
        val inputAmount = binding.etBaseAmount.text.toString().toDouble()
        val tipPercent = binding.sbPercent.progress

        // Compute the tip
        val tipAmount = inputAmount * tipPercent / 100
        val totalAmount = inputAmount + tipAmount

        // Update UI
        binding.apply {
            tvTotalAmount.text = getString(R.string.string_format).format(tipAmount)
            tvTipAmount.text = getString(R.string.string_format).format(totalAmount)
        }
    }

    private val seekBarProgress = object : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            binding.apply {
                tvTipPercentLabel.text = getString(R.string.tip_percent_label_text, progress)
                updateTipDescription(progress)
                computeTipAndTotalAmount()
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

        override fun onStopTrackingTouch(seekBar: SeekBar?) {}

    }
}