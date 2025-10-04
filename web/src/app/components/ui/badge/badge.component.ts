import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

export type BadgeVariant =
  | 'success'
  | 'danger'
  | 'warning'
  | 'info'
  | 'primary'
  | 'secondary'
  | 'light'
  | 'dark';
export type BadgeSize = 'small' | 'medium' | 'large';

@Component({
  selector: 'app-badge',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './badge.component.html',
  styleUrls: ['./badge.component.css'],
})
export class BadgeComponent {
  @Input() variant: BadgeVariant = 'primary';
  @Input() size: BadgeSize = 'medium';
  @Input() pill: boolean = false;
  @Input() outline: boolean = false;
  @Input() clickable: boolean = false;
  @Input() tooltip: string = '';

  getBadgeClasses(): string {
    const classes = [`badge-${this.variant}`];

    if (this.size !== 'medium') {
      classes.push(`badge-${this.size}`);
    }

    if (this.pill) {
      classes.push('badge-pill');
    }

    if (this.outline) {
      classes.push('badge-outline');
    }

    if (this.clickable) {
      classes.push('badge-clickable');
    }

    return classes.join(' ');
  }
}
