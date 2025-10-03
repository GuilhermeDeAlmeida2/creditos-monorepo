import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

export type MessageType = 'success' | 'error' | 'warning' | 'info';
export type MessageSize = 'small' | 'medium' | 'large';

@Component({
  selector: 'app-error-message',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './error-message.component.html',
  styleUrls: ['./error-message.component.css']
})
export class ErrorMessageComponent {
  @Input() type: MessageType = 'error';
  @Input() size: MessageSize = 'medium';
  @Input() dismissible: boolean = false;
  @Input() showIcon: boolean = true;
  @Input() title: string = '';
  @Input() message: string = '';
  @Input() autoHide: boolean = false;
  @Input() autoHideDelay: number = 5000; // 5 segundos

  isVisible: boolean = true;

  dismiss(): void {
    this.isVisible = false;
  }

  getIconClass(): string {
    const iconMap = {
      success: '✓',
      error: '✕',
      warning: '⚠',
      info: 'ℹ'
    };
    return iconMap[this.type];
  }

  getContainerClasses(): string {
    const classes = [`message-${this.type}`];
    
    if (this.size !== 'medium') {
      classes.push(`message-${this.size}`);
    }
    
    if (this.dismissible) {
      classes.push('message-dismissible');
    }
    
    return classes.join(' ');
  }
}
