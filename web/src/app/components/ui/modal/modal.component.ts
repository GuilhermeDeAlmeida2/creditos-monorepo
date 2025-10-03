import { Component, Input, Output, EventEmitter, TemplateRef, OnInit, OnDestroy, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface ModalButton {
  label: string;
  variant?: 'primary' | 'secondary' | 'success' | 'error' | 'info';
  disabled?: boolean;
  loading?: boolean;
  onClick: () => void;
}

export type ModalSize = 'small' | 'medium' | 'large' | 'fullscreen';
export type ModalPosition = 'center' | 'top' | 'bottom';

@Component({
  selector: 'app-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './modal.component.html',
  styleUrls: ['./modal.component.css']
})
export class ModalComponent implements OnInit, OnDestroy {
  @Input() isOpen: boolean = false;
  @Input() title: string = '';
  @Input() size: ModalSize = 'medium';
  @Input() position: ModalPosition = 'center';
  @Input() closable: boolean = true;
  @Input() backdrop: boolean = true;
  @Input() backdropClose: boolean = true;
  @Input() buttons: ModalButton[] = [];
  @Input() loading: boolean = false;
  @Input() showCloseButton: boolean = true;
  @Input() customHeader: TemplateRef<any> | null = null;
  @Input() customFooter: TemplateRef<any> | null = null;

  @Output() opened = new EventEmitter<void>();
  @Output() closed = new EventEmitter<void>();
  @Output() backdropClick = new EventEmitter<void>();

  private bodyScrollLocked = false;

  ngOnInit(): void {
    if (this.isOpen) {
      this.openModal();
    }
  }

  ngOnDestroy(): void {
    this.unlockBodyScroll();
  }

  @HostListener('document:keydown.escape', ['$event'])
  onEscapeKey(event: KeyboardEvent): void {
    if (this.isOpen && this.closable) {
      this.closeModal();
    }
  }

  openModal(): void {
    this.isOpen = true;
    this.lockBodyScroll();
    this.opened.emit();
  }

  closeModal(): void {
    this.isOpen = false;
    this.unlockBodyScroll();
    this.closed.emit();
  }

  onBackdropClick(): void {
    if (this.backdropClose && this.backdrop) {
      this.backdropClick.emit();
      this.closeModal();
    }
  }

  onButtonClick(button: ModalButton): void {
    if (!button.disabled && !button.loading) {
      button.onClick();
    }
  }

  private lockBodyScroll(): void {
    if (!this.bodyScrollLocked) {
      document.body.style.overflow = 'hidden';
      this.bodyScrollLocked = true;
    }
  }

  private unlockBodyScroll(): void {
    if (this.bodyScrollLocked) {
      document.body.style.overflow = '';
      this.bodyScrollLocked = false;
    }
  }

  getModalClasses(): string {
    const classes = ['modal'];
    classes.push(`modal-${this.size}`);
    classes.push(`modal-${this.position}`);
    
    if (this.isOpen) {
      classes.push('modal-open');
    }
    
    return classes.join(' ');
  }

  getBackdropClasses(): string {
    const classes = ['modal-backdrop'];
    
    if (this.isOpen) {
      classes.push('backdrop-open');
    }
    
    return classes.join(' ');
  }
}
