import { CurrencyFormatPipe } from './currency-format.pipe';

describe('CurrencyFormatPipe', () => {
  let pipe: CurrencyFormatPipe;

  beforeEach(() => {
    pipe = new CurrencyFormatPipe();
  });

  it('should create', () => {
    expect(pipe).toBeTruthy();
  });

  it('should format number as currency', () => {
    const result = pipe.transform(1234.56);
    expect(result).toContain('R$');
    expect(result).toContain('1.234,56');
  });

  it('should format string as currency', () => {
    const result = pipe.transform('1234.56');
    expect(result).toContain('R$');
    expect(result).toContain('1.234,56');
  });

  it('should handle null value', () => {
    const result = pipe.transform(null);
    expect(result).toContain('R$');
    expect(result).toContain('0,00');
  });

  it('should handle undefined value', () => {
    const result = pipe.transform(undefined);
    expect(result).toContain('R$');
    expect(result).toContain('0,00');
  });

  it('should handle empty string', () => {
    const result = pipe.transform('');
    expect(result).toContain('R$');
    expect(result).toContain('0,00');
  });

  it('should handle invalid string', () => {
    const result = pipe.transform('invalid');
    expect(result).toContain('R$');
    expect(result).toContain('0,00');
  });

  it('should format zero', () => {
    const result = pipe.transform(0);
    expect(result).toContain('R$');
    expect(result).toContain('0,00');
  });

  it('should format large numbers', () => {
    const result = pipe.transform(1234567.89);
    expect(result).toContain('R$');
    expect(result).toContain('1.234.567,89');
  });

  it('should use custom currency', () => {
    const result = pipe.transform(100, 'USD');
    expect(result).toContain('US$');
    expect(result).toContain('100,00');
  });
});
