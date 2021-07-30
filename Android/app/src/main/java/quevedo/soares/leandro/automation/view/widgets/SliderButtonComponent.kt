package quevedo.soares.leandro.automation.view.widgets

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.graphics.minus
import quevedo.soares.leandro.automation.R
import kotlin.math.abs

class SliderButtonComponent : View {

	private var primaryColor = 0
	private var secondaryColor = 0
	private val radius = 10
	private val textSize = 20
	var text: String = ""

	// region Paint definition
	private val foregroundPaint by lazy {
		Paint().apply {
			this.isAntiAlias = true
			this.color = secondaryColor
			this.isAntiAlias = true
			this.style = Paint.Style.FILL
		}
	}

	private val backgroundPaint by lazy {
		Paint(foregroundPaint).apply {
			this.color = primaryColor
		}
	}

	private val foregroundTextPaint by lazy {
		Paint().apply {
			this.typeface = Typeface.create(context.resources.getFont(R.font.permanent_marker), Typeface.BOLD)
			this.style = Paint.Style.FILL
			this.textSize = this@SliderButtonComponent.textSize * context.resources.displayMetrics.scaledDensity
			this.color = primaryColor
		}
	}

	private val backgroundTextPaint by lazy {
		Paint(foregroundTextPaint).apply {
			this.color = secondaryColor
		}
	}
	// endregion

	private var _value = 0.0f
	var value
		get() = this._value
		set(v) {
			setCurrentValue(v)
		}

	private var progress = 0f
	var minValue = 0.0f
	var maxValue = 100.0f
	var onChangeListener: ((Float) -> Unit)? = null

	private var initialPosition: PointF = PointF(0f, 0f)
	private var isDragging = false

	// region Constructors
	constructor(context: Context) : super(context)

	constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
		setupAttributes(attrs)
	}

	constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
		setupAttributes(attrs, defStyleAttr)
	}

	constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
		setupAttributes(attrs, defStyleAttr, defStyleRes)
	}
	// endregion

	init {
		// Parent setup
		this.elevation = 2 * this.context.resources.displayMetrics.density

		setWillNotDraw(false)
	}

	private fun setupAttributes(attributeSet: AttributeSet?, defStyleAttr: Int = 0, defStyleRes: Int = 0) {
		if (attributeSet == null) return

		val attributes = context.theme.obtainStyledAttributes(attributeSet, R.styleable.SliderButtonComponent, defStyleAttr, defStyleRes)

		attributes.getString(R.styleable.SliderButtonComponent_android_text)?.let { this.text = it }
		attributes.getFloat(R.styleable.SliderButtonComponent_android_min, 0f).let { this.minValue = it }
		attributes.getFloat(R.styleable.SliderButtonComponent_android_max, 100f).let { this.maxValue = it }
		attributes.getFloat(R.styleable.SliderButtonComponent_android_value, 0f).let { this.value = it }
		this.primaryColor = attributes.getColor(R.styleable.SliderButtonComponent_colorBackground, Color.RED)
		this.secondaryColor = attributes.getColor(R.styleable.SliderButtonComponent_colorForeground, Color.BLUE)

		if (isInEditMode && this.text.isEmpty()) this.text = "Slider"

		attributes.recycle()

		// Only for preview
		if (isInEditMode) {
			this.progress = 0.5f
			this._value = 50f
			this.minValue = 0f
			this.maxValue = 100f
		}
	}

	private fun clamp(x: Float, min: Float, max: Float) = if (x > max) max else if (x < min) min else x

	@SuppressLint("ClickableViewAccessibility")
	override fun onTouchEvent(event: MotionEvent?): Boolean {
		event?.let {
			fun processEvent(horizontalPosition: Float) {
				// Calculate the progress of the slider
				progress = horizontalPosition / this.width

				// Locate it to the min and max values
				val newValue = clamp(progress * this.maxValue, this.minValue, this.maxValue)
				if (newValue != _value) {
					_value = newValue

					this.invalidate()
				}
			}

			// Define the events to be consumed
			val consumedEvents = arrayOf(MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP, MotionEvent.ACTION_DOWN)
			if (!consumedEvents.contains(event.action)) return@let

			// Get the position into an pointF
			val position = PointF(event.x, event.y)

			when (event.action) {
				MotionEvent.ACTION_DOWN -> {
					// Stores the initial position of the first touch, to calculate the travel
					this.initialPosition = position

					return true
				}

				MotionEvent.ACTION_MOVE -> {
					// If it isn't dragging yet, check if the amount of horizontal travel was greater than the vertical travel distance
					if (!isDragging) {
						val distance = position - this.initialPosition

						// Detect horizontal dragging
						if (abs(distance.x) > abs(distance.y)) {
							// Start dragging
							isDragging = true
						} else return false
					}

					// Disallow parent view (e.g scroll views and view pagers) to steal touch events from this view
					this.parent?.requestDisallowInterceptTouchEvent(true)

					processEvent(position.x)

					return true
				}

				MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
					processEvent(position.x)

					// Fires the onChangeListener
					this.onChangeListener?.invoke(_value)

					// Stop dragging
					isDragging = false

					return true
				}
			}

		}

		return false
	}

	private fun getTextBounds(): Rect {
		val rect = Rect()
		foregroundTextPaint.getTextBounds(text, 0, text.length, rect)

		return rect
	}

	private fun createRectPath(rect: RectF, radius: Float) = Path().apply { addRoundRect(rect, radius, radius, Path.Direction.CW) }

	override fun draw(canvas: Canvas?) {
		if (canvas == null) return super.draw(canvas)

		// Calculate variables
		val width = this.width.toFloat()
		val foregroundWidth = this.progress * this.width
		val height = this.height.toFloat()
		val bounds = RectF(0f, 0f, width, height)
		val foregroundBounds = RectF(0f, 0f, foregroundWidth, height)
		val cornerRadius = this.radius * context.resources.displayMetrics.density
		val textBounds = getTextBounds()
		val textX = width / 2f - textBounds.width() / 2f
		val textY = height / 2 + textBounds.height() / 2

		// Clip all the canvas
		canvas.save()
		canvas.clipPath(createRectPath(bounds, cornerRadius))
		// Draw the background shape
		canvas.drawRoundRect(bounds, cornerRadius, cornerRadius, backgroundPaint)
		// Draw the foreground shape
		canvas.drawRect(foregroundBounds, foregroundPaint)
		canvas.restore()

		if (text.isNotEmpty()) {
			// Draw the background text
			canvas.save()
			canvas.clipOutPath(createRectPath(foregroundBounds, cornerRadius))
			canvas.drawText(text, textX, textY, backgroundTextPaint)
			canvas.restore()

			// Draw the foreground text
			canvas.save()
			canvas.clipPath(createRectPath(foregroundBounds, cornerRadius))
			canvas.drawText(text, textX, textY, foregroundTextPaint)
			canvas.restore()
		}
	}

	private fun setCurrentValue(value: Float) {
		this._value = this.clamp(value, this.minValue, this.maxValue)
		this.progress = value / this.maxValue
		this.invalidate()
	}

	fun setValueAnimated(value: Float) {
		ValueAnimator.ofFloat(_value, value).apply {
			duration = 500
			interpolator = AccelerateDecelerateInterpolator()

			addUpdateListener {
				setCurrentValue(it.animatedValue as Float)
			}
		}.start()
	}

}




