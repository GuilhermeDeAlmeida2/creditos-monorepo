import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, PingResponse, Credito } from './api.service';
import { CreditosComponent } from './creditos/creditos.component';
import { ButtonComponent, ButtonVariant } from './components/ui/button/button.component';
import { InputComponent } from './components/ui/input/input.component';
import { BadgeComponent } from './components/ui/badge/badge.component';
import { ErrorMessageComponent } from './components/ui/error-message/error-message.component';
import { CardComponent } from './components/ui/card/card.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, CreditosComponent, ButtonComponent, InputComponent, BadgeComponent, ErrorMessageComponent, CardComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  activeTab: 'creditos' | 'buscar-credito' = 'creditos';
  loading = false;
  result: string | null = null;
  isError = false;

  // Propriedades para busca por número do crédito
  numeroCredito: string = '';
  creditoDetalhes: Credito | null = null;
  loadingCredito: boolean = false;
  errorMessageCredito: string = '';

  constructor(private apiService: ApiService) {}

  pingApi(): void {
    this.loading = true;
    this.result = null;
    this.isError = false;

    this.apiService.ping().subscribe({
      next: (response: PingResponse) => {
        this.result = JSON.stringify(response, null, 2);
        this.isError = false;
        this.loading = false;
      },
      error: (error) => {
        this.result = `Erro: ${error.message || 'Falha na comunicação com a API'}`;
        this.isError = true;
        this.loading = false;
      }
    });
  }

  // Métodos para busca por número do crédito
  buscarCreditoPorNumero(): void {
    if (!this.numeroCredito.trim()) {
      this.errorMessageCredito = 'Por favor, digite um número de crédito válido.';
      return;
    }

    this.loadingCredito = true;
    this.errorMessageCredito = '';
    this.creditoDetalhes = null;

    this.apiService.buscarCreditoPorNumero(this.numeroCredito.trim()).subscribe({
      next: (response) => {
        this.creditoDetalhes = response;
        this.loadingCredito = false;
      },
      error: (error) => {
        if (error.status === 404) {
          this.errorMessageCredito = `Nenhum crédito encontrado para o número: ${this.numeroCredito}`;
        } else {
          this.errorMessageCredito = `Erro ao buscar crédito: ${error.message || 'Erro interno do servidor'}`;
        }
        this.loadingCredito = false;
      }
    });
  }


  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('pt-BR');
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  }

  getConnectionButtonVariant(): ButtonVariant {
    if (this.isError) return 'error';
    if (this.result) return 'success';
    return 'primary';
  }
}
