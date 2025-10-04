import { DateFormatPipe } from './date-format.pipe';

describe('DateFormatPipe', () => {
  let pipe: DateFormatPipe;

  beforeEach(() => {
    pipe = new DateFormatPipe();
  });

  it('should create', () => {
    expect(pipe).toBeTruthy();
  });

  it('should format date string', () => {
    const result = pipe.transform('2023-12-25');
    expect(result).toMatch(/\d{2}\/\d{2}\/2023/);
  });

  it('should format Date object', () => {
    const date = new Date('2023-12-25');
    const result = pipe.transform(date);
    expect(result).toMatch(/\d{2}\/\d{2}\/2023/);
  });

  it('should handle null value', () => {
    expect(pipe.transform(null)).toBe('');
  });

  it('should handle undefined value', () => {
    expect(pipe.transform(undefined)).toBe('');
  });

  it('should handle invalid date string', () => {
    expect(pipe.transform('invalid-date')).toBe('');
  });

  it('should format with short format', () => {
    const result = pipe.transform('2023-12-25', 'short');
    expect(result).toMatch(/\d{2}\/\d{2}\/2023/);
  });

  it('should format with long format', () => {
    const result = pipe.transform('2023-12-25', 'long');
    expect(result).toContain('dezembro');
  });

  it('should format with datetime format', () => {
    const result = pipe.transform('2023-12-25T10:30:00', 'datetime');
    expect(result).toContain('25/12/2023');
  });

  it('should format with time format', () => {
    const result = pipe.transform('2023-12-25T10:30:00', 'time');
    expect(result).toContain('10:30');
  });
});
