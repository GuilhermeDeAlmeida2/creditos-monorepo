import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonComponent, ButtonVariant } from '../ui/button/button.component';
import { ApiService, TestDataResponse } from '../../api.service';

@Component({
  selector: 'app-connection-status',
  standalone: true,
  imports: [CommonModule, ButtonComponent],
  templateUrl: './connection-status.component.html',
  styleUrls: ['./connection-status.component.css'],
})
export class ConnectionStatusComponent {
  @Input() loading: boolean = false;
  @Input() result: string | null = null;
  @Input() isError: boolean = false;

  @Output() connectionTest = new EventEmitter<void>();
  @Output() testDataGenerated = new EventEmitter<string>();
  @Output() testDataDeleted = new EventEmitter<string>();

  generatingTestData: boolean = false;
  deletingTestData: boolean = false;

  constructor(private apiService: ApiService) {}

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

  onGenerateTestData(): void {
    this.generatingTestData = true;
    
    this.apiService.gerarRegistrosTeste().subscribe({
      next: (response: TestDataResponse) => {
        this.generatingTestData = false;
        const message = response.erro || 
          `${response.mensagem} - ${response.registrosGerados} registros gerados.`;
        this.testDataGenerated.emit(message);
      },
      error: (error) => {
        this.generatingTestData = false;
        this.testDataGenerated.emit(`Erro ao gerar registros: ${error.message}`);
      }
    });
  }

  onDeleteTestData(): void {
    this.deletingTestData = true;
    
    this.apiService.deletarRegistrosTeste().subscribe({
      next: (response: TestDataResponse) => {
        this.deletingTestData = false;
        const message = response.erro || 
          `${response.mensagem} - ${response.registrosDeletados} registros deletados.`;
        this.testDataDeleted.emit(message);
      },
      error: (error) => {
        this.deletingTestData = false;
        this.testDataDeleted.emit(`Erro ao deletar registros: ${error.message}`);
      }
    });
  }
}
