import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

export type CardVariant = 'default' | 'elevated' | 'outlined' | 'flat';
export type CardSize = 'small' | 'medium' | 'large';

@Component({
  selector: 'app-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.css'],
})
export class CardComponent {
  @Input() variant: CardVariant = 'default';
  @Input() size: CardSize = 'medium';
  @Input() title: string = '';
  @Input() subtitle: string = '';
  @Input() showHeader: boolean = true;
  @Input() showFooter: boolean = false;
  @Input() clickable: boolean = false;
  @Input() hoverable: boolean = false;

  getCardClasses(): string {
    const classes = [`card-${this.variant}`, `card-${this.size}`];

    if (this.clickable) {
      classes.push('card-clickable');
    }

    if (this.hoverable) {
      classes.push('card-hoverable');
    }


    return classes.join(' ');
  }
}
