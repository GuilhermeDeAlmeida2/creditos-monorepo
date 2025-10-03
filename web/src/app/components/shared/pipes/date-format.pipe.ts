import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'dateFormat',
  standalone: true
})
export class DateFormatPipe implements PipeTransform {
  transform(value: string | Date | null | undefined, format: string = 'short'): string {
    if (!value) {
      return '';
    }

    const date = typeof value === 'string' ? new Date(value) : value;
    
    if (isNaN(date.getTime())) {
      return '';
    }

    switch (format) {
      case 'short':
        return date.toLocaleDateString('pt-BR');
      case 'long':
        return date.toLocaleDateString('pt-BR', {
          year: 'numeric',
          month: 'long',
          day: 'numeric'
        });
      case 'datetime':
        return date.toLocaleString('pt-BR');
      case 'time':
        return date.toLocaleTimeString('pt-BR');
      default:
        return date.toLocaleDateString('pt-BR');
    }
  }
}
