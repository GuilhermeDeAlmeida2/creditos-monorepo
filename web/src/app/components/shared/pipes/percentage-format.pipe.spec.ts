import { PercentageFormatPipe } from './percentage-format.pipe';

describe('PercentageFormatPipe', () => {
  let pipe: PercentageFormatPipe;

  beforeEach(() => {
    pipe = new PercentageFormatPipe();
  });

  it('should create', () => {
    expect(pipe).toBeTruthy();
  });

  it('should format number as percentage', () => {
    const result = pipe.transform(15);
    expect(result).toContain('15');
    expect(result).toContain('%');
  });

  it('should format string as percentage', () => {
    const result = pipe.transform('15.5');
    expect(result).toContain('15');
    expect(result).toContain('%');
  });

  it('should handle null value', () => {
    const result = pipe.transform(null);
    expect(result).toContain('0');
    expect(result).toContain('%');
  });

  it('should handle undefined value', () => {
    const result = pipe.transform(undefined);
    expect(result).toContain('0');
    expect(result).toContain('%');
  });

  it('should handle empty string', () => {
    const result = pipe.transform('');
    expect(result).toContain('0');
    expect(result).toContain('%');
  });

  it('should handle invalid string', () => {
    const result = pipe.transform('invalid');
    expect(result).toContain('0');
    expect(result).toContain('%');
  });

  it('should format zero', () => {
    const result = pipe.transform(0);
    expect(result).toContain('0');
    expect(result).toContain('%');
  });

  it('should format with custom decimals', () => {
    expect(pipe.transform(15.567, 1)).toBe('15,6%');
  });

  it('should format large numbers', () => {
    const result = pipe.transform(100);
    expect(result).toContain('100');
    expect(result).toContain('%');
  });

  it('should format decimal numbers', () => {
    expect(pipe.transform(15.75)).toBe('15,75%');
  });
});
