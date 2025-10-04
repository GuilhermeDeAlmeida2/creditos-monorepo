import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface Tab {
  id: string;
  label: string;
  icon: string;
}

@Component({
  selector: 'app-tab-navigation',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './tab-navigation.component.html',
  styleUrls: ['./tab-navigation.component.css'],
})
export class TabNavigationComponent {
  @Input() activeTab: string = 'creditos';
  @Output() tabChange = new EventEmitter<string>();

  tabs: Tab[] = [
    { id: 'creditos', label: 'Buscar Créditos', icon: '📋' },
    { id: 'buscar-credito', label: 'Buscar por Número do Crédito', icon: '🔍' },
  ];

  onTabChange(tabId: string): void {
    this.tabChange.emit(tabId);
  }
}
