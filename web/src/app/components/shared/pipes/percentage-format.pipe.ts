import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'percentageFormat',
  standalone: true,
})
export class PercentageFormatPipe implements PipeTransform {
  transform(value: number | string | null | undefined, decimals: number = 2): string {
    if (value === null || value === undefined || value === '') {
      return '0%';
    }

    const numericValue = typeof value === 'string' ? parseFloat(value) : value;

    if (isNaN(numericValue)) {
      return '0%';
    }

    return new Intl.NumberFormat('pt-BR', {
      style: 'percent',
      minimumFractionDigits: decimals,
      maximumFractionDigits: decimals,
    }).format(numericValue / 100);
  }
}
