import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

export type LoadingType = 'spinner' | 'dots' | 'pulse' | 'skeleton';
export type LoadingSize = 'small' | 'medium' | 'large';
export type LoadingColor = 'primary' | 'secondary' | 'success' | 'warning' | 'danger' | 'info';

@Component({
  selector: 'app-loading',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './loading.component.html',
  styleUrls: ['./loading.component.css'],
})
export class LoadingComponent {
  @Input() type: LoadingType = 'spinner';
  @Input() size: LoadingSize = 'medium';
  @Input() color: LoadingColor = 'primary';
  @Input() text: string = '';
  @Input() overlay: boolean = false;
  @Input() fullScreen: boolean = false;

  getLoadingClasses(): string {
    const classes = [`loading-${this.type}`, `loading-${this.size}`, `loading-${this.color}`];

    if (this.overlay) {
      classes.push('loading-overlay');
    }

    if (this.fullScreen) {
      classes.push('loading-fullscreen');
    }

    return classes.join(' ');
  }
}
