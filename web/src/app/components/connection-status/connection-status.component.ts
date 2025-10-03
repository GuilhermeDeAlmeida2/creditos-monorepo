import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonComponent, ButtonVariant } from '../ui/button/button.component';

@Component({
  selector: 'app-connection-status',
  standalone: true,
  imports: [CommonModule, ButtonComponent],
  templateUrl: './connection-status.component.html',
  styleUrls: ['./connection-status.component.css']
})
export class ConnectionStatusComponent {
  @Input() loading: boolean = false;
  @Input() result: string | null = null;
  @Input() isError: boolean = false;
  
  @Output() connectionTest = new EventEmitter<void>();

  getConnectionButtonVariant(): ButtonVariant {
    if (this.isError) return 'error';
    if (this.result) return 'success';
    return 'primary';
  }

  getButtonText(): string {
    if (this.loading) return 'Testando...';
    if (this.result) return 'Conectado';
    return 'Testar Conexão';
  }

  onConnectionTest(): void {
    this.connectionTest.emit();
  }
}
