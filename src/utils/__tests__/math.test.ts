import { add, multiply, divide } from '../math'

describe('Math utilities', () => {
  describe('add', () => {
    it('adds two positive numbers correctly', () => {
      expect(add(2, 3)).toBe(5)
    })

    it('adds negative numbers correctly', () => {
      expect(add(-2, -3)).toBe(-5)
    })

    it('adds mixed positive and negative numbers', () => {
      expect(add(5, -3)).toBe(2)
    })
  })

  describe('multiply', () => {
    it('multiplies two positive numbers correctly', () => {
      expect(multiply(3, 4)).toBe(12)
    })

    it('multiplies by zero', () => {
      expect(multiply(5, 0)).toBe(0)
    })

    it('multiplies negative numbers', () => {
      expect(multiply(-3, -4)).toBe(12)
    })
  })

  describe('divide', () => {
    it('divides two positive numbers correctly', () => {
      expect(divide(10, 2)).toBe(5)
    })

    it('throws error when dividing by zero', () => {
      expect(() => divide(10, 0)).toThrow('Cannot divide by zero')
    })

    it('divides negative numbers', () => {
      expect(divide(-10, 2)).toBe(-5)
    })
  })
})
