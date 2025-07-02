# Sports Management App - Figma Design Specifications

## 🎨 Design System Overview

This document outlines the complete UI/UX design specifications for the Sports Management Android application. The design follows Material Design 3 principles with a modern, accessible, and user-friendly approach.

## 📐 Design Specifications

### Grid System & Layout
- **Base Unit**: 8dp grid system
- **Margins**: 16dp (consistent throughout the app)
- **Card Padding**: 16dp internal padding
- **Element Spacing**: 8dp, 16dp, 24dp
- **Screen Padding**: 16dp horizontal, 8dp vertical

### Typography Scale
```
Display Large: 57sp (Roboto, Regular)
Display Medium: 45sp (Roboto, Regular)
Display Small: 36sp (Roboto, Regular)

Headline Large: 32sp (Roboto, Medium)
Headline Medium: 28sp (Roboto, Medium)
Headline Small: 24sp (Roboto, Medium)

Title Large: 22sp (Roboto, Medium)
Title Medium: 16sp (Roboto, Medium)
Title Small: 14sp (Roboto, Medium)

Label Large: 14sp (Roboto, Medium)
Label Medium: 12sp (Roboto, Medium)
Label Small: 11sp (Roboto, Medium)

Body Large: 16sp (Roboto, Regular)
Body Medium: 14sp (Roboto, Regular)
Body Small: 12sp (Roboto, Regular)
```

### Color Palette

#### Primary Colors
```
Primary: #2E7D32 (Sports Green)
On Primary: #FFFFFF
Primary Container: #A5D6A7
On Primary Container: #1B5E20

Secondary: #FF5722 (Energy Orange)
On Secondary: #FFFFFF
Secondary Container: #FFCCBC
On Secondary Container: #BF360C
```

#### Surface Colors
```
Surface: #FEFBFF
On Surface: #1C1B1F
Surface Variant: #F3F3F3
On Surface Variant: #49454F

Background: #FAFAFA
On Background: #1C1B1F
```

#### Sport Category Colors
```
Chess: #8E24AA (Purple)
Carom: #D32F2F (Red)
Table Tennis: #1976D2 (Blue)
Badminton: #388E3C (Green)
Cricket: #F57C00 (Orange)
Billiards: #5D4037 (Brown)
```

#### Status Colors
```
Success: #4CAF50
Warning: #FF9800
Error: #F44336
Info: #2196F3
Live: #F44336 (with pulsing animation)
```

## 📱 Screen Designs

### 1. Home Screen (Dashboard)
```
Layout Structure:
┌─────────────────────────────┐
│ Toolbar (Sports Management) │
├─────────────────────────────┤
│ Welcome Card (Gradient)     │
│ ┌─ Welcome Message         │
│ └─ User Greeting           │
├─────────────────────────────┤
│ Live Matches Section        │
│ ┌─ Section Header + Dot    │
│ ├─ Horizontal RecyclerView │
│ └─ "No Live Matches"       │
├─────────────────────────────┤
│ Featured Sports (2x2 Grid) │
│ ┌─ Chess    │ Carom       │
│ ├─ T.Tennis │ Badminton   │
├─────────────────────────────┤
│ Upcoming Matches            │
│ ┌─ Vertical List           │
│ └─ Match Cards             │
├─────────────────────────────┤
│ Recent Results              │
│ └─ Match Result Cards       │
└─────────────────────────────┘
```

**Components:**
- **Welcome Card**: Gradient background (Primary to Primary Container)
- **Live Matches**: Horizontal scrollable cards with live indicator
- **Featured Sports**: 2x2 grid with sport icons and participant counts
- **Match Cards**: Player names, sport, time, status indicator

### 2. Categories Screen
```
Layout Structure:
┌─────────────────────────────┐
│ Toolbar (Sport Categories)  │
├─────────────────────────────┤
│ Category Grid (2 columns)   │
│ ┌─────────┬─────────────┐  │
│ │ Chess   │ Carom       │  │
│ │ Icon    │ Icon        │  │
│ │ 16/32   │ 8/16        │  │
│ │ [Reg]   │ [Register]  │  │
│ ├─────────┼─────────────┤  │
│ │ T.Tennis│ Badminton   │  │
│ │ Icon    │ Icon        │  │
│ │ 20/32   │ 15/32       │  │
│ │ [Reg]   │ [Register]  │  │
│ └─────────┴─────────────┘  │
└─────────────────────────────┘
```

**Components:**
- **Category Cards**: 
  - Sport icon (color-coded)
  - Sport name (Title Medium)
  - Participant count progress
  - Registration button (Primary/Secondary)
  - Registration status indicator

### 3. Matches Screen
```
Layout Structure:
┌─────────────────────────────┐
│ Toolbar (Matches)           │
├─────────────────────────────┤
│ Tab Layout                  │
│ [All] [Live] [Today] [Mine] │
├─────────────────────────────┤
│ Match List                  │
│ ┌─────────────────────────┐ │
│ │ Player1 VS Player2      │ │
│ │ Chess • 14:30          │ │
│ │ [LIVE] Score: 2-1      │ │
│ └─────────────────────────┘ │
│ ┌─────────────────────────┐ │
│ │ Player3 VS Player4      │ │
│ │ Badminton • Tomorrow   │ │
│ │ [SCHEDULED]            │ │
│ └─────────────────────────┘ │
└─────────────────────────────┘
```

**Components:**
- **Tab Layout**: Material tabs for filtering
- **Match Cards**: 
  - Player names (Body Large)
  - Sport and time (Body Small)
  - Status chip with appropriate color
  - Live score (if applicable)

### 4. Profile Screen
```
Layout Structure:
┌─────────────────────────────┐
│ Toolbar (Profile)           │
├─────────────────────────────┤
│ User Info Card              │
│ ┌─ Avatar (Circular)       │
│ ├─ Name (Headline Small)   │
│ ├─ Email (Body Medium)     │
│ └─ [Edit Profile]          │
├─────────────────────────────┤
│ Quick Stats Cards           │
│ ┌─ Registered │ Matches    │
│ │ Categories  │ Played     │
│ └─ 4          │ 12         │
├─────────────────────────────┤
│ Menu Items                  │
│ ┌─ My Registrations        │
│ ├─ Match History           │
│ ├─ Admin Panel (if admin)  │
│ ├─ Settings                │
│ └─ Logout                  │
└─────────────────────────────┘
```

## 🎯 Component Library

### Cards
```
Material Card Specifications:
- Corner Radius: 12dp
- Elevation: 4dp
- Background: Surface color
- Padding: 16dp
- Margin: 8dp (between cards)
```

### Buttons
```
Primary Button:
- Background: Primary color
- Text: On Primary color
- Corner Radius: 8dp
- Min Height: 48dp
- Padding: 16dp horizontal

Secondary Button:
- Background: Transparent
- Border: 2dp Primary color
- Text: Primary color
- Corner Radius: 8dp
```

### Status Chips
```
Live Chip:
- Background: Error color
- Text: "LIVE"
- Icon: Pulsing dot
- Corner Radius: 16dp

Scheduled Chip:
- Background: Info color
- Text: "SCHEDULED"
- Corner Radius: 16dp

Completed Chip:
- Background: Success color
- Text: "COMPLETED"
- Corner Radius: 16dp
```

### Icons
```
Icon Specifications:
- Size: 24dp (default), 48dp (category icons)
- Style: Material Design icons
- Colors: On Surface (default), category-specific
```

## 🔄 Animations & Interactions

### Transitions
```
Card Hover/Press:
- Elevation: 4dp → 8dp
- Duration: 150ms
- Curve: Fast out, slow in

Button Press:
- Scale: 1.0 → 0.95
- Duration: 100ms
- Ripple effect: On Primary Container

Page Transitions:
- Slide animation
- Duration: 300ms
- Shared element transitions for images
```

### Live Updates
```
Live Indicator:
- Pulsing animation
- Scale: 0.8 → 1.2 → 0.8
- Duration: 1000ms
- Infinite repeat

Score Updates:
- Number change animation
- Scale up slightly on change
- Color flash to indicate update
```

## 📏 Responsive Design

### Phone Portrait (360dp+)
- Single column layout
- Full-width cards
- Stacked navigation

### Phone Landscape
- Maintain portrait layout
- Adjust toolbar height
- Optimize scroll areas

### Tablet (600dp+)
- Two-column layout for categories
- Side navigation option
- Larger card sizes
- More content per screen

## ♿ Accessibility

### Touch Targets
- Minimum size: 48dp x 48dp
- Adequate spacing between targets
- Clear visual feedback

### Contrast Ratios
- Text on background: 4.5:1 minimum
- Large text: 3:1 minimum
- Icons: 3:1 minimum

### Content Descriptions
- All images have content descriptions
- Interactive elements have proper labels
- Screen reader optimization

## 🎨 Dark Mode Support

### Dark Theme Colors
```
Primary: #81C784 (Lighter green)
Background: #121212
Surface: #1E1E1E
On Surface: #E1E1E1
Surface Variant: #2D2D2D
```

## 📐 Figma File Structure

```
Pages:
├── 🎨 Design System
│   ├── Colors
│   ├── Typography
│   ├── Components
│   └── Icons
├── 📱 Mobile Screens
│   ├── Home/Dashboard
│   ├── Categories
│   ├── Matches
│   ├── Profile
│   └── Match Details
├── 🌊 User Flows
│   ├── Registration Flow
│   ├── Match Viewing Flow
│   └── Admin Flow
└── 📋 Specs & Documentation
    ├── Measurements
    ├── Export Settings
    └── Developer Handoff
```

## 🔧 Implementation Notes

### Android XML Layouts
- Use ConstraintLayout for complex layouts
- RecyclerView for lists with proper ViewHolders
- CardView for consistent card styling
- Material Components for buttons and inputs

### Key Measurements
```xml
<!-- Margins and Padding -->
<dimen name="margin_standard">16dp</dimen>
<dimen name="margin_small">8dp</dimen>
<dimen name="margin_large">24dp</dimen>

<!-- Card Specifications -->
<dimen name="card_corner_radius">12dp</dimen>
<dimen name="card_elevation">4dp</dimen>

<!-- Text Sizes -->
<dimen name="text_size_headline">24sp</dimen>
<dimen name="text_size_title">18sp</dimen>
<dimen name="text_size_body">16sp</dimen>
<dimen name="text_size_caption">14sp</dimen>
```

## 🎨 Export Settings

### Android Resources
- Icons: Vector Drawables (24dp, 48dp)
- Images: WebP format, multiple densities
- Colors: colors.xml with semantic naming
- Dimensions: dimens.xml with consistent values

### Asset Naming Convention
```
Icons: ic_[name]_[size]
Images: img_[context]_[description]
Colors: color_[semantic_name]
Layouts: layout_[type]_[name]
```

This design system ensures a cohesive, modern, and accessible user experience across the entire Sports Management application, following Material Design 3 principles while maintaining a unique sports-focused identity.