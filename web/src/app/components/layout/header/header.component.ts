import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ConnectionStatusComponent } from '../../connection-status/connection-status.component';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, ConnectionStatusComponent],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent {
  @Input() title: string = 'Sistema de Créditos Constituídos';
  @Input() subtitle: string = 'Gerenciamento e consulta de créditos por NFS-e';
  @Input() loading: boolean = false;
  @Input() result: string | null = null;
  @Input() isError: boolean = false;

  @Output() connectionTest = new EventEmitter<void>();
  @Output() testDataGenerated = new EventEmitter<string>();
  @Output() testDataDeleted = new EventEmitter<string>();

  onConnectionTest(): void {
    this.connectionTest.emit();
  }

  onTestDataGenerated(message: string): void {
    this.testDataGenerated.emit(message);
  }

  onTestDataDeleted(message: string): void {
    this.testDataDeleted.emit(message);
  }
}
