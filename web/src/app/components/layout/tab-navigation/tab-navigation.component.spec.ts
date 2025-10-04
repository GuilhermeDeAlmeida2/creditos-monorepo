import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TabNavigationComponent } from './tab-navigation.component';

describe('TabNavigationComponent', () => {
  let component: TabNavigationComponent;
  let fixture: ComponentFixture<TabNavigationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TabNavigationComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TabNavigationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have default active tab', () => {
    expect(component.activeTab).toBe('creditos');
  });

  it('should have two tabs', () => {
    expect(component.tabs.length).toBe(2);
    expect(component.tabs[0].id).toBe('creditos');
    expect(component.tabs[1].id).toBe('buscar-credito');
  });

  it('should emit tab change event when tab is clicked', () => {
    spyOn(component.tabChange, 'emit');

    component.onTabChange('buscar-credito');

    expect(component.tabChange.emit).toHaveBeenCalledWith('buscar-credito');
  });
});
