import { Component, Input, Output, EventEmitter, TemplateRef } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface TableColumn {
  key: string;
  label: string;
  sortable?: boolean;
  width?: string;
  align?: 'left' | 'center' | 'right';
  template?: TemplateRef<any>;
  render?: (value: any, row: any) => string;
}

export interface TableAction {
  label: string;
  icon?: string;
  variant?: 'primary' | 'secondary' | 'success' | 'error' | 'info';
  disabled?: boolean;
  onClick: (row: any) => void;
}

@Component({
  selector: 'app-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.css'],
})
export class TableComponent {
  @Input() columns: TableColumn[] = [];
  @Input() data: any[] = [];
  @Input() loading: boolean = false;
  @Input() emptyMessage: string = 'Nenhum dado encontrado';
  @Input() striped: boolean = true;
  @Input() hover: boolean = true;
  @Input() actions: TableAction[] = [];
  @Input() selectable: boolean = false;
  @Input() selectedRows: any[] = [];

  @Output() rowClick = new EventEmitter<any>();
  @Output() rowSelect = new EventEmitter<any[]>();
  @Output() sort = new EventEmitter<{ column: string; direction: 'asc' | 'desc' }>();

  currentSort: { column: string; direction: 'asc' | 'desc' } | null = null;

  onRowClick(row: any): void {
    this.rowClick.emit(row);
  }

  onRowSelect(row: any, event: Event): void {
    const target = event.target as HTMLInputElement;
    if (target.checked) {
      this.selectedRows.push(row);
    } else {
      this.selectedRows = this.selectedRows.filter(r => r !== row);
    }
    this.rowSelect.emit(this.selectedRows);
  }

  onSort(column: TableColumn): void {
    if (!column.sortable) return;

    let direction: 'asc' | 'desc' = 'asc';
    if (this.currentSort?.column === column.key && this.currentSort.direction === 'asc') {
      direction = 'desc';
    }

    this.currentSort = { column: column.key, direction };
    this.sort.emit(this.currentSort);
  }

  getSortIcon(column: TableColumn): string {
    if (!column.sortable) return '';
    if (this.currentSort?.column !== column.key) return '↕️';
    return this.currentSort.direction === 'asc' ? '↑' : '↓';
  }

  onActionClick(action: TableAction, row: any, event: Event): void {
    event.stopPropagation();
    if (!action.disabled) {
      action.onClick(row);
    }
  }

  onSelectAll(event: Event): void {
    const target = event.target as HTMLInputElement;
    if (target.checked) {
      this.selectedRows = [...this.data];
    } else {
      this.selectedRows = [];
    }
    this.rowSelect.emit(this.selectedRows);
  }

  trackByFn(index: number, item: any): any {
    return item.id || index;
  }
}
