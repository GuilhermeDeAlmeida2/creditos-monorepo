import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, PingResponse, Credito } from './api.service';
import { HeaderComponent } from './components/layout/header/header.component';
import { TabNavigationComponent } from './components/layout/tab-navigation/tab-navigation.component';
import { FooterComponent } from './components/layout/footer/footer.component';
import { CreditosDetailsComponent } from './components/features/creditos-details/creditos-details.component';
import { CreditoSearchComponent } from './components/features/credito-search/credito-search.component';
import { ButtonComponent, ButtonVariant } from './components/ui/button/button.component';
import { InputComponent } from './components/ui/input/input.component';
import { BadgeComponent } from './components/ui/badge/badge.component';
import { ErrorMessageComponent } from './components/ui/error-message/error-message.component';
import { CardComponent } from './components/ui/card/card.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    HeaderComponent,
    TabNavigationComponent,
    FooterComponent,
    CreditosDetailsComponent,
    CreditoSearchComponent,
    ButtonComponent,
    InputComponent,
    BadgeComponent,
    ErrorMessageComponent,
    CardComponent,
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  activeTab: 'creditos' | 'buscar-credito' = 'creditos';
  loading = false;
  result: string | null = null;
  isError = false;
  testDataMessage: string | null = null;
  showTestDataMessage = false;

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
      error: error => {
        this.result = `Erro: ${error.message || 'Falha na comunicação com a API'}`;
        this.isError = true;
        this.loading = false;
      },
    });
  }

  getConnectionButtonVariant(): ButtonVariant {
    if (this.isError) return 'error';
    if (this.result) return 'success';
    return 'primary';
  }

  onTabChange(tabId: string): void {
    this.activeTab = tabId as 'creditos' | 'buscar-credito';
  }

  onTestDataGenerated(message: string): void {
    this.testDataMessage = message;
    this.showTestDataMessage = true;
    // Auto-hide message after 5 seconds
    setTimeout(() => {
      this.showTestDataMessage = false;
    }, 5000);
  }

  onTestDataDeleted(message: string): void {
    this.testDataMessage = message;
    this.showTestDataMessage = true;
    // Auto-hide message after 5 seconds
    setTimeout(() => {
      this.showTestDataMessage = false;
    }, 5000);
  }

  dismissTestDataMessage(): void {
    this.showTestDataMessage = false;
  }
}
