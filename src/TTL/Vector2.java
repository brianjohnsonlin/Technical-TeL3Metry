/*
 * The MIT License (MIT)
 *
 * Copyright © 2015-2017, Heiko Brumme
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package TTL;

import java.nio.FloatBuffer;

/**
 * This class represents a (x,y)-Vector. GLSL equivalent to vec2.
 *
 * @author Heiko Brumme
 */
public class Vector2 {

  public float x;
  public float y;

  /**
   * Creates a default 2-tuple vector with all values set to 0.
   */
  public Vector2() {
    this.x = 0f;
    this.y = 0f;
  }

  /**
   * Creates a 2-tuple vector with specified values.
   *
   * @param x x value
   * @param y y value
   */
  public Vector2(float x, float y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Calculates the squared length of the vector.
   *
   * @return Squared length of this vector
   */
  public float lengthSquared() {
    return x * x + y * y;
  }

  /**
   * Calculates the length of the vector.
   *
   * @return Length of this vector
   */
  public float length() {
    return (float) Math.sqrt(lengthSquared());
  }

  /**
   * Normalizes the vector.
   *
   * @return Normalized vector
   */
  public Vector2 normalize() {
    float length = length();
    return divide(length);
  }

  /**
   * Adds this vector to another vector.
   *
   * @param other The other vector
   *
   * @return Sum of this + other
   */
  public Vector2 add(Vector2 other) {
    float x = this.x + other.x;
    float y = this.y + other.y;
    return new Vector2(x, y);
  }

  /**
   * Negates this vector.
   *
   * @return Negated vector
   */
  public Vector2 negate() {
    return scale(-1f);
  }

  /**
   * Subtracts this vector from another vector.
   *
   * @param other The other vector
   *
   * @return Difference of this - other
   */
  public Vector2 subtract(Vector2 other) {
    return this.add(other.negate());
  }

  /**
   * Multiplies a vector by a scalar.
   *
   * @param scalar Scalar to multiply
   *
   * @return Scalar product of this * scalar
   */
  public Vector2 scale(float scalar) {
    float x = this.x * scalar;
    float y = this.y * scalar;
    return new Vector2(x, y);
  }

  /**
   * Divides a vector by a scalar.
   *
   * @param scalar Scalar to multiply
   *
   * @return Scalar quotient of this / scalar
   */
  public Vector2 divide(float scalar) {
    return scale(1f / scalar);
  }

  /**
   * Calculates the dot product of this vector with another vector.
   *
   * @param other The other vector
   *
   * @return Dot product of this * other
   */
  public float dot(Vector2 other) {
    return this.x * other.x + this.y * other.y;
  }

  /**
   * Calculates a linear interpolation between this vector with another
   * vector.
   *
   * @param other The other vector
   * @param alpha The alpha value, must be between 0.0 and 1.0
   *
   * @return Linear interpolated vector
   */
  public Vector2 lerp(Vector2 other, float alpha) {
    return this.scale(1f - alpha).add(other.scale(alpha));
  }

  /**
   * Stores the vector in a given Buffer.
   *
   * @param buffer The buffer to store the vector data
   */
  public void toBuffer(FloatBuffer buffer) {
    buffer.put(x).put(y);
    buffer.flip();
  }

  public Vector2 clone() {
    return new Vector2(x, y);
  }

  public void replaceWith(Vector2 other) {
    x = other.x;
    y = other.y;
  }

}
