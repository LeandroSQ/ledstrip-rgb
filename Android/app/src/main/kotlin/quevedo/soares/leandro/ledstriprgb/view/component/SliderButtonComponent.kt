package quevedo.soares.leandro.ledstriprgb.view.component

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewOutlineProvider
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.graphics.minus
import quevedo.soares.leandro.ledstriprgb.R
import quevedo.soares.leandro.ledstriprgb.extension.clamp
import kotlin.math.abs

// Constant to determine which events should be processed
private val CONSUMED_EVENTS = arrayOf(
	MotionEvent.ACTION_MOVE,
	MotionEvent.ACTION_UP,
	MotionEvent.ACTION_DOWN
)

@Suppress("MemberVisibilityCanBePrivate")
class SliderButtonComponent : View {

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

	// region Variables
	private var primaryColor = 0
	private var secondaryColor = 0
	private val radius = 10
	private val textSize = 20
	var text: String = ""

	private var _value = 0.0f
	var value
		get() = this._value
		set(v) {
			setCurrentValue(v)
		}

	var minValue = 0.0f
	var maxValue = 100.0f
	var onChangeListener: ((Float) -> Unit)? = null

	private var progress = 0f

	private var initialPosition: PointF = PointF(0f, 0f)
	private var isDragging = false
	// endregion

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

	// region Initial setup
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
	// endregion

	// region Utilities
	private fun createRectPath(rect: RectF, radius: Float) = Path().apply { addRoundRect(rect, radius, radius, Path.Direction.CW) }
	// endregion

	// region Touch handling
	private fun processTouchEvent(horizontalPosition: Float) {
		// Calculate the progress of the slider
		progress = horizontalPosition / this.width

		// Locate it to the min and max values
		val newValue = (progress * this.maxValue) clamp this.minValue..this.maxValue
		if (newValue != _value) {
			_value = newValue

			this.invalidate()
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	override fun onTouchEvent(event: MotionEvent?): Boolean {
		event?.let {
			// Ignore useless events
			if (!CONSUMED_EVENTS.contains(event.action)) return@let

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

						// Discards vertical dragging
						if (abs(distance.x) <= abs(distance.y)) return false

						// Start horizontal dragging
						isDragging = true
					}

					// Disallow parent view (e.g scroll views and view pagers) to steal touch events from this view
					this.parent?.requestDisallowInterceptTouchEvent(true)

					this.processTouchEvent(position.x)

					return true
				}

				MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
					this.processTouchEvent(position.x)

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
	// endregion

	// region Drawing
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
	// endregion

	// region Exposed methods
	private fun setCurrentValue(value: Float) {
		this._value = value clamp this.minValue..this.maxValue
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
	// endregion

	// region Save and restore instance logic
	override fun onSaveInstanceState(): Parcelable {
		return Bundle().apply {
			putParcelable("super", super.onSaveInstanceState())
			putFloat("value", _value)
		}
	}

	override fun onRestoreInstanceState(state: Parcelable?) {
		if (state != null && state is Bundle) {
			_value = state.getFloat("value")
			super.onRestoreInstanceState(state.getParcelable("super"))
		} else {
			super.onRestoreInstanceState(state)
		}
	}
	// endregion

	// region View sizing
	private fun getTextBounds(): Rect {
		val rect = Rect()
		foregroundTextPaint.getTextBounds(text, 0, text.length, rect)

		return rect
	}

	override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
		super.onSizeChanged(w, h, oldw, oldh)

		val radius = this.radius * context.resources.displayMetrics.density
		outlineProvider = OutlineProvider(w, h, radius)
	}

	/***
	 * Class responsible for providing the outline of the custom view
	 *
	 * So the O.S can pre-process as it sees fit, like elevation shadow, for instance
	 ***/
	internal class OutlineProvider(private val width: Int, private val height: Int, private val radius: Float) : ViewOutlineProvider() {

		override fun getOutline(view: View?, outline: Outline?) {
			outline?.setRoundRect(0, 0, width, height, radius)
		}

	}
	// endregion

}